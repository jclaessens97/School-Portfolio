package be.kdg.cluedobackend.services.impl;


import be.kdg.cluedobackend.config.CluedoProperties;
import be.kdg.cluedobackend.controllers.messagehandlers.MessageHandler;
import be.kdg.cluedobackend.controllers.messagehandlers.TurnMessageHandler;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.notebook.NoteBook;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import be.kdg.cluedobackend.services.BoardService;
import be.kdg.cluedobackend.services.CardService;
import be.kdg.cluedobackend.services.LobbyService;
import be.kdg.cluedobackend.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LobbyServiceImpl implements LobbyService {
    private final CluedoRepository cluedoRepository;
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final CardService cardService;
    private final NoteService noteService;
    private final CluedoProperties cluedoProperties;
    private final MessageHandler<Cluedo> lobbyMessageHandler;
    private final BoardService boardService;
    private final TurnMessageHandler turnMessageHandler;

    @Autowired
    public LobbyServiceImpl(
            CluedoRepository cluedoRepository,
            PlayerRepository playerRepository,
            UserRepository userRepository,
            CardService cardService,
            NoteService noteService,
            CluedoProperties cluedoProperties,
            BoardService boardService,
            MessageHandler<Cluedo> lobbyMessageHandler,
            TurnMessageHandler turnMessageHandler
    ) {
        this.cluedoRepository = cluedoRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.cardService = cardService;
        this.noteService =  noteService;
        this.cluedoProperties = cluedoProperties;
        this.boardService = boardService;
        this.lobbyMessageHandler = lobbyMessageHandler;
        this.turnMessageHandler = turnMessageHandler;
    }

    @PostConstruct
    public void init() {
        noteService.setLobbyService(this);
    }

    //#region CRUD
    @Override
    public Cluedo getLobbyById(int cluedoId) throws CluedoException {
        Optional<Cluedo> lobby = cluedoRepository.findById(cluedoId);

        if (lobby.isEmpty()) {
            throw new CluedoException(
                CluedoExceptionType.CLUEDO_NOT_FOUND,
                String.format("Game with id %d not found.", cluedoId)
            );
        }

        return lobby.get();
    }

    @Override
    public List<Cluedo> getAllLobbies() {
        return cluedoRepository.findAllByActiveIsFalse();
    }

    @Override
    public List<Cluedo> getAllLobbiesNotJoined(UUID userId) {
        List<Cluedo> lobbies = cluedoRepository.findAllByActiveIsFalse();
        return lobbies
                .stream()
                .filter(l -> l.getPlayers().stream()
                        .map(p -> p.getUser())
                        .filter(u -> u.getUserId().equals(userId))
                        .findFirst().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Cluedo> getAllLobbiesJoined(UUID userId) {
        List<Cluedo> lobbies = cluedoRepository.findAllByActiveIsFalse();
        return lobbies
                .stream()
                .filter(l -> !l.getPlayers().stream()
                        .map(p -> p.getUser())
                        .filter(u -> u.getUserId().equals(userId))
                        .findFirst().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<Cluedo> getOpenGames(UUID userId) {
        return cluedoRepository.findAllByPlayers_User_UserIdAndActiveIsTrue(userId);
    }

    @Override
    public int createLobby(UUID userId, CharacterType characterType) throws CluedoException {
        User user = getUserById(userId);
        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        Player player = new Player(user, cluedo, characterType);
        cluedo.setHost(player);
        return cluedoRepository.save(cluedo).getCluedoId();
    }

    @Override
    public void joinLobby(int cluedoId, UUID userId, CharacterType characterType) throws CluedoException {
        User user = getUserById(userId);
        Cluedo cluedo = getLobbyById(cluedoId);

        checkIfGameIsActive(cluedo);
        checkIfGameIsFull(cluedo);
        checkIfCharacterTypeIsAvailable(cluedo, characterType);
        checkIfAlreadyInGame(cluedo, user);


        Player player = new Player(user, cluedo, characterType);
        cluedo.joinPlayer(player);
        cluedoRepository.save(cluedo);
        lobbyMessageHandler.sendMessage(cluedoId, cluedo, null);
    }

    @Override
    public void leaveLobby(int cluedoId, int playerId, UUID userId, boolean isKick) throws CluedoException {
        Cluedo cluedo = getLobbyById(cluedoId);

        Player player = getPlayerById(playerId);
        if (!cluedo.isPlayerInGame(player)) {
            throw new CluedoException(
                    CluedoExceptionType.PLAYER_NOT_IN_LOBBY,
                    String.format("Player with id %d not in game", playerId)
            );
        }

        checkIfGameIsActive(cluedo);

        if (isKick && currentUserIsNotHost(cluedo, cluedo.getHost().getUserId())) {
            throw new CluedoException(
                    CluedoExceptionType.PLAYER_NOT_HOST,
                    "User can only kick if host"
            );
        } else if (!isKick && !player.getUserId().equals(userId)) {
            throw new CluedoException(
                    CluedoExceptionType.PLAYER_USER_NOT_LINKED,
                    "Player leaving is linked to different user"
            );
        }

        cluedo.removePlayer(player);

        if (cluedo.isHost(player) && cluedo.hasPlayersLeft()) {
            cluedo.assignNewHost();
        }

        if (!cluedo.hasPlayersLeft()) {
            cluedoRepository.delete(cluedo);
        } else {
            cluedoRepository.save(cluedo);
        }
        lobbyMessageHandler.sendMessage(cluedoId, cluedo, null);
    }

    @Override
    public void changeGameSettings(int cluedoId, UUID userId, String lobbyName, int turnDuration, int maxPlayers)
            throws CluedoException {
        Cluedo cluedo = getLobbyById(cluedoId);

        if (currentUserIsNotHost(cluedo, userId)) {
            throw new CluedoException(CluedoExceptionType.PLAYER_NOT_HOST);
        }

        checkIfGameIsActive(cluedo);

        if (lobbyName == null || lobbyName.trim().isEmpty()) {
            throw new CluedoException(CluedoExceptionType.LOBBY_NAME_EMPTY);
        }

        if (turnDuration < 1) {
            throw new CluedoException(CluedoExceptionType.LOBBY_TURN_DURATION_TOO_SHORT);
        }

        if (maxPlayers > cluedoProperties.getMaxPlayers() || maxPlayers < 3) {
            throw new CluedoException(CluedoExceptionType.LOBBY_MAX_PLAYERS_INVALID);
        }

        cluedo.setLobbyName(lobbyName);
        cluedo.setTurnDuration(turnDuration);
        cluedo.setMaxPlayers(maxPlayers);
        cluedoRepository.save(cluedo);

        lobbyMessageHandler.sendMessage(cluedoId, cluedo, null);
    }

    @Override
    public Cluedo startGame(int cluedoId, UUID userId) throws CluedoException {
        Cluedo cluedo = getLobbyById(cluedoId);

        if (cluedo.getPlayers().size() < 3 || cluedo.getPlayers().size() > cluedoProperties.getMaxPlayers()){
            throw new CluedoException(CluedoExceptionType.LOBBY_START_PLAYER_COUNT);
        }
        if (currentUserIsNotHost(cluedo, userId)) {
            throw new CluedoException(CluedoExceptionType.PLAYER_NOT_HOST);
        }

        cluedo.determinePlayerOrder();
        cluedo.switchTurn();
        turnMessageHandler.sendMessage(cluedoId, "new", null);


        Scene caseFile = cardService.createSolutionScene();
        cardService.dealCardsToPlayers(cluedo.getPlayers(), caseFile);
        cluedo.setCaseFile(caseFile);
        cluedo.setActive(true);

        int playerCount = cluedo.getPlayers().size();
        for (Player player : cluedo.getPlayers()) {
            NoteBook notebook = noteService.initializeNoteBook(playerCount);
            player.setNoteBook(notebook);
        }

        boardService.setupBoard(cluedoId);
        lobbyMessageHandler.sendMessage(cluedoId, cluedo, null);
        return cluedoRepository.save(cluedo);
    }

    @Override
    public Player getPlayerInLobby(Cluedo cluedo, UUID userId) throws CluedoException {
        Optional<Player> optionalPlayer = cluedo.getPlayers().stream().filter(p -> p.getUser().getUserId().equals(userId)).findFirst();
        if (optionalPlayer.isEmpty()) throw new CluedoException(CluedoExceptionType.PLAYER_NOT_IN_LOBBY);
        return optionalPlayer.get();
    }
    //#endregion

    @Override
    public List<CharacterType> getFreeCharacters(int cluedoId) throws CluedoException {
        Cluedo cluedo = getLobbyById(cluedoId);
        return getFreeCharacters(cluedo);
    }

    //#region HELPERS
    private List<CharacterType> getFreeCharacters(Cluedo cluedo) {
        List<CharacterType> characterTypes = new LinkedList<>(Arrays.asList(CharacterType.values()));
        cluedo.getPlayers().forEach( p-> characterTypes.remove(p.getCharacterType()));
        return characterTypes;
    }

    private void checkIfGameIsActive(Cluedo cluedo) throws CluedoException {
        if (cluedo.isActive()) {
            throw new CluedoException(CluedoExceptionType.GAME_ALREADY_STARTED,
                String.format("Game with id %d has already started.", cluedo.getCluedoId())
            );
        }
    }

    private void checkIfGameIsFull(Cluedo cluedo) throws CluedoException {
        if (cluedo.isFull()) {
            throw new CluedoException(CluedoExceptionType.LOBBY_FULL,
                String.format("Game with id %d is full.", cluedo.getCluedoId())
            );
        }
    }

    private void checkIfAlreadyInGame(Cluedo cluedo, User user) throws CluedoException {
        List<Player> players = cluedo.getPlayers().stream().filter(p -> p.getUserId().equals(user.getUserId())).collect(Collectors.toList());
        if (!players.isEmpty()) throw new CluedoException(CluedoExceptionType.PLAYER_ALREADY_IN_LOBBY);
    }

    private void checkIfCharacterTypeIsAvailable(Cluedo cluedo, CharacterType characterType) throws CluedoException {
        List<CharacterType> availableTypes = getFreeCharacters(cluedo);
        if (!availableTypes.contains(characterType)){
            throw new CluedoException(CluedoExceptionType.CHARACTERTYPE_TAKEN);
        }
    }

    private User getUserById(UUID userId) throws CluedoException {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CluedoException(CluedoExceptionType.USER_NOT_FOUND,
                    String.format("User with id %s not found.", userId)
            ));
    }

    private Player getPlayerById(int playerId) throws CluedoException {
        return playerRepository.findById(playerId)
            .orElseThrow(() -> new CluedoException(CluedoExceptionType.PLAYER_NOT_FOUND,
                    String.format("Player with id %d not found", playerId)
            ));
    }

    private boolean currentUserIsNotHost(Cluedo cluedo, UUID userId) {
        return !cluedo.getHost().getUserId().equals(userId);
    }
    //#endregion HELPERS
}
