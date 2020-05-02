package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.ChoiceDto;
import be.kdg.cluedobackend.dto.LocationDto;
import be.kdg.cluedobackend.dto.MoveDto;
import be.kdg.cluedobackend.dto.PossibilitiesDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Turn;
import be.kdg.cluedobackend.model.gameboard.GameBoard;
import be.kdg.cluedobackend.model.gameboard.Room;
import be.kdg.cluedobackend.model.gameboard.Tile;
import be.kdg.cluedobackend.services.BoardService;
import be.kdg.cluedobackend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/board")
public class BoardApiController {
    private final BoardService boardService;
    private final GameService gameService;

    @Autowired
    public BoardApiController(
            BoardService boardService,
            GameService gameService
    ) {
        this.boardService = boardService;
        this.gameService = gameService;
    }

    @GetMapping("/game/{id}")
    public ResponseEntity<GameBoard> getBoard(@PathVariable int id){
        try {
            return ResponseEntity.ok(boardService.getBoardByGameId(id));
        } catch (CluedoException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/characterType")
    public ResponseEntity<CharacterType> getCharactertype(@RequestParam("id") int id){
        try {
            return ResponseEntity.ok(gameService.getCharacterType(id));
        } catch (CluedoException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/possibilities")
    public ResponseEntity<PossibilitiesDto> getPossibilities(@RequestParam("type") String type, @RequestParam("game") int gameId) throws CluedoException {
        Turn turn = gameService.getCurrentTurn(gameId);
        CharacterType cType = CharacterType.valueOf(type);
        boolean hasTurn = turn.getPlayer().getCharacterType() == cType;
        if(hasTurn){
            Room roomWithPassage = boardService.getPassage(cType, gameId);
            int thrownDice = turn.getDiceTotal() == null ? 0 : turn.getDiceTotal();
            LocationDto location = new LocationDto(turn.getXCoord(), turn.getYCoord());
            boolean movesPossible = boardService.getPossibleMoves(cType,3,gameId).size() > 0;
            PossibilitiesDto dto = new PossibilitiesDto(true, movesPossible, roomWithPassage,location,thrownDice);
            return ResponseEntity.ok(dto);
        }
        PossibilitiesDto dto = new PossibilitiesDto(false, false, null, null, 0);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/positions")
    public ResponseEntity<Set<Tile>> findPossiblePositions(@RequestParam("type") String type, @RequestParam("game") int gameId, @RequestParam("roll") int rolled) throws CluedoException {
        gameService.validChoice(new ChoiceDto(CharacterType.valueOf(type), gameId));
        gameService.updateDiceRoll(gameId, rolled);
        return ResponseEntity.ok(boardService.getPossibleMoves(CharacterType.valueOf(type),rolled,gameId));
    }

    @PutMapping("/takePassage")
    public ResponseEntity<Room> takePassage(@RequestBody ChoiceDto choice) throws CluedoException {
        gameService.validChoice(choice);
        Room room = boardService.takePassage(choice.getType(), choice.getGameId());
        gameService.updateChosenCoordinates(choice.getGameId(), room.getXCoord(), room.getYCoord());
        return ResponseEntity.ok(room);
    }

    @PutMapping("/move")
    public ResponseEntity<Tile> move(@RequestBody MoveDto move) throws CluedoException {
        gameService.validChoice(move);
        Tile tile = boardService.moveCharacter(move.getType(), move.getLocation().getX(), move.getLocation().getY(),move.getGameId());
        gameService.updateChosenCoordinates(move.getGameId(), tile.getXCoord(), tile.getYCoord());
        return ResponseEntity.ok(tile);
    }

    @PutMapping("/endTurn")
    public ResponseEntity<Boolean> endTurn(@RequestBody ChoiceDto choice) throws CluedoException {
        gameService.validChoice(choice);
        gameService.finishTurn(choice.getGameId());
        return ResponseEntity.ok(true);
    }
}
