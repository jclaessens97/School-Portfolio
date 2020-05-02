package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.controllers.messagehandlers.RefreshGameMessageHandler;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.helpers.DefaultBoardInitializer;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.gameboard.Character;
import be.kdg.cluedobackend.model.gameboard.GameBoard;
import be.kdg.cluedobackend.model.gameboard.Room;
import be.kdg.cluedobackend.model.gameboard.Tile;
import be.kdg.cluedobackend.repository.BoardRepository;
import be.kdg.cluedobackend.repository.CharacterRepository;
import be.kdg.cluedobackend.repository.TileRepository;
import be.kdg.cluedobackend.services.BoardService;
import org.neo4j.driver.internal.InternalNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class BoardServiceImpl implements BoardService {
    private final TileRepository tileRepository;
    private final BoardRepository boardRepository;
    private final CharacterRepository characterRepository;
    private final RefreshGameMessageHandler refreshGameMessageHandler;

    @Autowired
    public BoardServiceImpl(
            TileRepository tileRepository,
            BoardRepository boardRepository,
            CharacterRepository characterRepository,
            RefreshGameMessageHandler refreshGameMessageHandler) {
        this.tileRepository = tileRepository;
        this.boardRepository = boardRepository;
        this.characterRepository = characterRepository;
        this.refreshGameMessageHandler = refreshGameMessageHandler;
    }

    @Override
    @PostConstruct
    public void initialiseBoard() {
        GameBoard defaultGameboard = boardRepository.findByName("Default");
        if (defaultGameboard == null) {
            boardRepository.removeAll();
            GameBoard board = DefaultBoardInitializer.initialiseDefault();
            board.initialiseCharacters(0);
            boardRepository.save(board);
        }
    }

    @Override
    public void setupBoard(String boardName, int gameId) {
        try {
            getBoardByGameId(gameId);
        } catch (CluedoException cex) {
            if (cex.getCluedoExceptionType() == CluedoExceptionType.BOARD_NOT_FOUND) {
                GameBoard board = boardRepository.findByName(boardName);
                board.initialiseCharacters(gameId);
                boardRepository.save(board);
            }
        }
    }

    @Override
    public void setupBoard(int gameId) {
        setupBoard("Default", gameId);
    }

    @Override
    public GameBoard getBoardByGameId(int gameId) throws CluedoException {
        GameBoard board = boardRepository.findByGameId(gameId);
        if (board == null) {
            throw new CluedoException(
                    CluedoExceptionType.BOARD_NOT_FOUND,
                    String.format("There is no board with id %d", gameId)
            );
        }

        board.setCharacters(characterRepository.findCharactersByGameId(gameId));
        return board;
    }

    private Tile convertNode(InternalNode node) {
        Function<InternalNode, RoomType> toRoom = n -> RoomType.valueOf(n.asMap().get("roomType").toString());
        BiFunction<InternalNode, String, Integer> toInt = (n, key) -> Integer.parseInt(n.asMap().get(key).toString());
        if (!node.asMap().containsKey("roomType"))
            return new Tile(toInt.apply(node, "xCoord"), toInt.apply(node, "yCoord"), node.id());
        return new Room(toInt.apply(node, "xCoord"), toInt.apply(node, "yCoord"), toRoom.apply(node), node.id());
    }

    @Override
    public Set<Tile> getPossibleMoves(CharacterType type, int step, int gameId) throws CluedoException {
        //GET ALL PATHS WITH A LENGTH OF {step}
        Stream<List<Tile>> paths = tileRepository.findPositionsInRange(type, step, gameId).stream().map(path ->
                path.stream().map(tileNode -> convertNode((InternalNode) tileNode)).collect(Collectors.toList()));

        //GET ALL LOCATIONS OF ALL THE CHARACTERS
        Set<Character> characters = characterRepository.findCharactersByGameId(gameId);
        Set<Tile> charLocations = characters.stream().map(Character::getPosition).collect(Collectors.toSet());
        Tile current = characters.stream().filter(character -> character.getCharacterType() == type).findFirst().get().getPosition();

        //SHORTEN PATH TO FIRST ROOM OR END OF PATH
        Stream<List<Tile>> shortenedPaths = paths.map(path -> {
            Optional<Tile> roomOpt = path.stream().filter(tile -> tile instanceof Room).findFirst();
            return roomOpt.map(room -> path.subList(0, path.indexOf(room) + 1)).orElse(path);
        });

        //FILTER PATHS WHERE THERE IS A CHARACTER ON THE TILE (NOT ROOM)
        Predicate<Tile> condition = location -> !charLocations.contains(location) || location instanceof Room;
        Stream<Tile> destinations = shortenedPaths.filter(path -> path.size() > 0)
                .filter(path -> path.size() == path.stream().filter(condition).count())
                .map(path -> path.get(path.size() - 1));

        //Return all destinations that are not the current location
        return destinations.filter(t -> !(current.sameLocation(t))).collect(Collectors.toSet());
    }

    @Override
    public Tile moveCharacter(CharacterType type, int newX, int newY, int gameId) throws CluedoException {
        Tile tile = tileRepository.moveCharacter(type, newX, newY, gameId);
        refreshGameMessageHandler.sendMessage(gameId, null, null);
        return tile;
    }

    @Override
    public Tile moveCharacter(CharacterType characterType, RoomType roomType, int gameId) throws CluedoException {
        Tile tile = tileRepository.moveToRoom(characterType, roomType, gameId);
        refreshGameMessageHandler.sendMessage(gameId, null, null);
        return tile;
    }

    @Override
    public Room getPassage(CharacterType type, int gameId) {
        return tileRepository.getPassage(type, gameId);
    }

    @Override
    public Room takePassage(CharacterType type, int gameId) {
        return tileRepository.moveThroughPassage(type, gameId);
    }
}
