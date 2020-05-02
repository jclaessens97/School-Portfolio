package be.kdg.cluedobackend.controllers.api;

import be.kdg.cluedobackend.dto.CardDto;
import be.kdg.cluedobackend.dto.PlayerDto;
import be.kdg.cluedobackend.dto.TurnDto;
import be.kdg.cluedobackend.dto.suggestion.ReceivingSuggestionDto;
import be.kdg.cluedobackend.dto.suggestion.SuggestionReplyDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.CharacterCard;
import be.kdg.cluedobackend.model.cards.RoomCard;
import be.kdg.cluedobackend.model.cards.WeaponCard;
import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.cards.types.WeaponType;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.game.SceneType;
import be.kdg.cluedobackend.model.game.Turn;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.services.BoardService;
import be.kdg.cluedobackend.services.CardService;
import be.kdg.cluedobackend.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
public class GameApiController {
    private final GameService gameService;
    private final BoardService boardService;
    private final CardService cardService;
    private final ObjectMapper objectMapper;

    @Autowired
    public GameApiController(GameService gameService, BoardService boardService, CardService cardService, ObjectMapper objectMapper) {
        this.boardService = boardService;
        this.gameService = gameService;
        this.cardService = cardService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/turn")
    public ResponseEntity<TurnDto> getTurn(@RequestParam int gameId) throws CluedoException {
        Turn currentTurn = gameService.getCurrentTurn(gameId);
        int remainingTime =  (int) ChronoUnit.MINUTES.between(LocalDateTime.now(), currentTurn.getStartTurn().plusMinutes(currentTurn.getMaxTurnTime()));
        Player currentPlayer = currentTurn.getPlayer();
        PlayerDto player = new PlayerDto(currentPlayer.getUser().getUserName(), currentPlayer.getCharacterType(),currentPlayer.getPlayerId());
        TurnDto dto = new TurnDto(player, remainingTime);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/cards")
    public ResponseEntity<List<CardDto>> getCards() {
        List<CardDto> cardDtoList = new ArrayList<>();
        for (Card card : cardService.getAllCards()) {
            cardDtoList.add(new CardDto(card.getCardId(), card.getCardType(), card.getText(), card.getUrl()));
        }
        return ResponseEntity.ok(cardDtoList);
    }

    @PutMapping("/suggestion/reply")
    public ResponseEntity replySuggestion(@RequestBody SuggestionReplyDto suggestionReplyDto) throws CluedoException {
        Card card = null;
        CardDto cardDto = suggestionReplyDto.getCard();
        switch (cardDto.getCardType()){
            case CHARACTER:
                card = new CharacterCard(CharacterType.valueOf(cardDto.getType().toUpperCase()), cardDto.getUrl());
                break;
            case WEAPON:
                card = new WeaponCard(WeaponType.valueOf(cardDto.getType().toUpperCase()), cardDto.getUrl());
                break;
            case ROOM:
                card = new RoomCard(RoomType.valueOf(cardDto.getType().toUpperCase()), cardDto.getUrl());
                break;
        }
        gameService.replySuggestion(card, suggestionReplyDto.getGameId(), cardDto);
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/suggestion")
    @SendTo("/topic/suggestion")
    @PostMapping("/suggestion")
    public ResponseEntity makeSuggestion(@RequestBody ReceivingSuggestionDto receivingSuggestionDto) throws CluedoException {
        List<CardDto> suggestionCards = receivingSuggestionDto.getSuggestionCards();
        Scene scene = makeScene(suggestionCards, SceneType.SUGGESTION);

        this.gameService.moveCharacter(receivingSuggestionDto.getSuggestionCards(), receivingSuggestionDto.getGameId(),scene);
        gameService.makeSuggestion(receivingSuggestionDto.getGameId(), scene, receivingSuggestionDto.getSuggestionCards());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ownCards")
    public ResponseEntity<List<CardDto>> getOwnCards(@RequestParam int gameId, @RequestParam int playerId) throws CluedoException {
        List<CardDto> cardDtoList = new ArrayList<>();
        for (Card card :  gameService.getPlayerCards(gameId, playerId)) {
            cardDtoList.add(new CardDto(card.getCardId(), card.getCardType(), card.getText(), card.getUrl()));
        }
        return ResponseEntity.ok(cardDtoList);
    }



    @PostMapping("/accusation")
    public ResponseEntity makeAccusation(@RequestBody ReceivingSuggestionDto suggestionDto) throws CluedoException, InterruptedException {
        List<CardDto> suggestionCards = suggestionDto.getSuggestionCards();

        Scene scene = makeScene(suggestionCards, SceneType.ACCUSATION);
        boolean hasEnded = gameService.makeAccusation(suggestionDto.getGameId(), scene, suggestionCards)[1];

        if (hasEnded) {
            Thread.sleep(5000);
            gameService.removePlayers(suggestionDto.getGameId());
        }

        return ResponseEntity.ok().build();
    }

    private Scene makeScene(List<CardDto> suggestionCards, SceneType sceneType) throws CluedoException {
        Optional<CardDto> characterCardDto = suggestionCards.stream().filter(c -> c.getCardType() == CardType.CHARACTER).findFirst();
        if (characterCardDto.isEmpty()) throw new CluedoException(CluedoExceptionType.INVALID_SUGGESTION);
        Optional<CardDto> weaponCardDto = suggestionCards.stream().filter(c -> c.getCardType() == CardType.WEAPON).findFirst();
        if (weaponCardDto.isEmpty()) throw new CluedoException(CluedoExceptionType.INVALID_SUGGESTION);
        Optional<CardDto> roomCardDto = suggestionCards.stream().filter(c -> c.getCardType() == CardType.ROOM).findFirst();
        if (roomCardDto.isEmpty()) throw new CluedoException(CluedoExceptionType.INVALID_SUGGESTION);
        Scene scene;
        try {
            CharacterCard characterCard = (CharacterCard) cardService.getCardById(characterCardDto.get().getCardId());
            WeaponCard weaponCard = (WeaponCard) cardService.getCardById(weaponCardDto.get().getCardId());
            RoomCard roomCard = (RoomCard) cardService.getCardById(roomCardDto.get().getCardId());
            scene = new Scene(characterCard, weaponCard, roomCard, sceneType);
        } catch (Exception e) {
            throw new CluedoException(CluedoExceptionType.INVALID_SUGGESTION);
        }
        return scene;
    }
}
