package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.controllers.messagehandlers.*;
import be.kdg.cluedobackend.dto.AccusationDto;
import be.kdg.cluedobackend.dto.CardDto;
import be.kdg.cluedobackend.dto.ChoiceDto;
import be.kdg.cluedobackend.dto.PlayerDto;
import be.kdg.cluedobackend.dto.suggestion.SceneDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.*;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import be.kdg.cluedobackend.services.BoardService;
import be.kdg.cluedobackend.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class GameServiceImpl implements GameService {
    private CluedoRepository cluedoRepository;
    private PlayerRepository playerRepository;
    private UserRepository userRepository;
    private RefreshGameMessageHandler refreshGameMessageHandler;
    private SuggestionMessageHandler suggestionMessageHandler;
    private SuggestionReplyHandler suggestionReplyHandler;
    private SuggestionReplyCardHandler suggestionReplyCardHandler;
    private AccusationMessageHandler accusationMessageHandler;
    private BoardService boardService;

    @Autowired
    public GameServiceImpl(CluedoRepository cluedoRepository, PlayerRepository playerRepository, UserRepository userRepository,
                           RefreshGameMessageHandler refreshGameMessageHandler, SuggestionReplyCardHandler suggestionReplyCardHandler, SuggestionMessageHandler suggestionMessageHandler, SuggestionReplyHandler suggestionReplyHandler,
                           AccusationMessageHandler accusationMessageHandler, TurnMessageHandler turnMessageHandler, BoardService boardService) {
        this.cluedoRepository = cluedoRepository;
        this.playerRepository = playerRepository;
        this.userRepository = userRepository;
        this.refreshGameMessageHandler = refreshGameMessageHandler;
        this.suggestionMessageHandler = suggestionMessageHandler;
        this.suggestionReplyHandler = suggestionReplyHandler;
        this.suggestionReplyCardHandler = suggestionReplyCardHandler;
        this.accusationMessageHandler = accusationMessageHandler;
        this.boardService = boardService;
    }

    @Override
    public Map<SuggestionType, Player> makeSuggestion(int gameId, Scene scene, List<CardDto> suggestionCards) throws CluedoException {
        if (scene.getSceneType() != SceneType.SUGGESTION) {
            throw new CluedoException(
                    CluedoExceptionType.SCENE_NOT_SUGGESTION,
                    "This scene is not a suggestion.");
        }
        Cluedo cluedo = getCluedoById(gameId);
        List<Player> players = cluedo.getPlayers();
        // al een suggestie gedaan
        if (cluedo.getCurrentTurn().getScene() != null) {
            return null;
        }

        cluedo.getCurrentTurn().setScene(scene);


        // Update gamestatistics
        updateGameStatistics(cluedo);

        // send all players the suggestion
        this.suggestionMessageHandler.sendMessage(gameId, new SceneDto(false, suggestionCards),null);
        boolean playerFoundWithMatchingCards;
        Map<SuggestionType, Player> exchangingPlayers;

        exchangingPlayers = loopOverEveryPlayer(cluedo, players, scene);
        playerFoundWithMatchingCards = !exchangingPlayers.isEmpty();

        if (playerFoundWithMatchingCards){
            // TODO send a websocket that no one has a card
            this.suggestionMessageHandler.sendMessage(cluedo.getCluedoId(), new SceneDto(false, new ArrayList<>()), null);
        }
        cluedoRepository.save(cluedo);
        return exchangingPlayers;
    }

    private void updateGameStatistics(Cluedo cluedo) {
        User user = cluedo.getCurrentTurn().getPlayer().getUser();
        int amountOfTurns = user.getGameStatistics().getAmountOfTurns();
        user.getGameStatistics().setAmountOfTurns(amountOfTurns+1);
        userRepository.save(user);
    }


    private Map<SuggestionType, Player> loopOverEveryPlayer(Cluedo cluedo, List<Player> players, Scene scene) {
        Map<SuggestionType, Player> exchangingPlayers = new HashMap<>();

        for (int i = 1; i < players.size(); i++) {
            int indexToCheck = (cluedo.getCurrentTurn().getPlayer().getGame_order() + i) % players.size();
            Player nextPlayer = players.get(indexToCheck);

            if (nextPlayer != cluedo.getCurrentTurn().getPlayer()) {
                Set<Card> matchingCards = findMatchingCards(nextPlayer, scene);
                exchangingPlayers = sendWebsocketsIfPlayerHasAMatchingCard(matchingCards, cluedo, nextPlayer, exchangingPlayers);
            }
        }
        return exchangingPlayers;
    }

    private Set<Card> findMatchingCards(Player nextPlayer, Scene scene) {
        Set<Card> matchingCards = new HashSet<>();
        for (Card card : scene.getAllCards()) {
            if (nextPlayer.hasCardInHand(card)) {
                matchingCards.add(card);
            }
        }
        return matchingCards;
    }


    private Map<SuggestionType, Player> sendWebsocketsIfPlayerHasAMatchingCard(Set<Card> matchingCards, Cluedo cluedo, Player nextPlayer, Map<SuggestionType, Player> exchangingPlayers) {
        if (matchingCards.size() > 0) {
            exchangingPlayers.put(SuggestionType.ASKING, cluedo.getCurrentTurn().getPlayer());
            exchangingPlayers.put(SuggestionType.RESPONDING, nextPlayer);
            cluedo.getCurrentTurn().setRespondant(nextPlayer);
            List<CardDto> matchingCardsDto = new ArrayList<>();
            for (Card matchingCard : matchingCards) {
                //matchingCardsDto.add(objectMapper.convertValue(matchingCard, CardDto.class));
                matchingCardsDto.add(new CardDto(matchingCard.getCardId(), matchingCard.getCardType(), matchingCard.getText(), matchingCard.getUrl()));
            }
            this.suggestionReplyHandler.sendMessage(cluedo.getCluedoId(), nextPlayer, null);
            if (!nextPlayer.isActive()) {
                Card card = matchingCards.stream().findFirst().get();
                CardDto cardDto = new CardDto(card.getCardId(), card.getCardType(), card.getText(), card.getUrl());
                this.suggestionReplyCardHandler.sendMessageId(cluedo.getCluedoId(), cluedo.getCurrentTurn().getPlayer().getPlayerId(), cardDto, null);
            } else {
                this.suggestionMessageHandler.sendMessageId(cluedo.getCluedoId(), nextPlayer.getPlayerId(), new SceneDto(false, matchingCardsDto), null);
            }
            //return true;
        }
        return exchangingPlayers;
    }



    @Override
    public boolean[] makeAccusation(int gameId, Scene scene, List<CardDto> suggestionCards) throws CluedoException {
        if (scene.getSceneType() != SceneType.ACCUSATION) {
            throw new CluedoException(
                    CluedoExceptionType.SCENE_NOT_ACCUSATION,
                    "This scene is not an accusation."
            );
        }
        this.suggestionMessageHandler.sendMessage(gameId,new SceneDto(true, suggestionCards), null);

        Cluedo cluedo = getCluedoById(gameId);
        boolean guess = cluedo.getCaseFile().equals(scene);
        boolean hasEnded = true;
        if (guess) {
            updateWinner(gameId);
        } else {
            hasEnded = updateLoser(gameId);
        }
        Player player = cluedo.getCurrentTurn().getPlayer();
        PlayerDto playerDto = new PlayerDto(player.getUser().getUserName(), player.getCharacterType(), player.getPlayerId());
        this.accusationMessageHandler.sendMessage(gameId, new AccusationDto(guess, playerDto, hasEnded), null);
        cluedoRepository.save(cluedo);
        return new boolean[] {guess, hasEnded};
    }

    private boolean updateLoser(int gameId) throws CluedoException {
        Cluedo cluedo =  getCluedoById(gameId);
        User user = cluedo.getCurrentTurn().getPlayer().getUser();
        float amountWrongAccusations = user.getGameStatistics().getWrongAccusations();
        float amountLosses = user.getGameStatistics().getLosses();
        user.getGameStatistics().setWrongAccusations(amountWrongAccusations+1);
        user.getGameStatistics().setLosses(amountLosses+1);
        userRepository.save(user);

        cluedo.getCurrentTurn().getPlayer().setActive(false);

        // if those are the last two active players then stop the game
        List<Player> activePlayers = cluedo.getPlayers().stream().filter(Player::isActive).collect(Collectors.toList());
        if (activePlayers.size() == 1 ) {
            Player winningPlayer = activePlayers.get(0);
            User winningUser = winningPlayer.getUser();
            float amountRightAccusations = winningUser.getGameStatistics().getRightAccusations();
            float amountWins = winningUser.getGameStatistics().getWins();
            winningUser.getGameStatistics().setRightAccusations(amountRightAccusations+1);
            winningUser.getGameStatistics().setWins(amountWins+1);
            cluedo.setWon(true);
            return true;
        }
        cluedoRepository.save(cluedo);
        return false;
    }

    private void updateWinner(int gameId) throws CluedoException {
        Cluedo cluedo = getCluedoById(gameId);
        List<Player> players = cluedo.getPlayers();
        User user = cluedo.getCurrentTurn().getPlayer().getUser();
        float amountRightAccusations = user.getGameStatistics().getRightAccusations();
        float amountWins = user.getGameStatistics().getWins();
        user.getGameStatistics().setRightAccusations(amountRightAccusations+1);
        user.getGameStatistics().setWins(amountWins+1);
        for (Player player : players) {
            if (player != cluedo.getCurrentTurn().getPlayer()) {
                User userToSave = player.getUser();
                float amountLosses = userToSave.getGameStatistics().getLosses();
                userToSave.getGameStatistics().setLosses(amountLosses+1);
                userRepository.save(userToSave);
            }
        }
        userRepository.save(user);

    }

    //#region CRUD
    @Override
    public void updateDiceRoll(int gameId, int roll) throws CluedoException{
        getActiveGame(gameId).getCurrentTurn().setDiceTotal(roll);
        cluedoRepository.saveAndFlush(getActiveGame(gameId));
    }

    @Override
    public void updateChosenCoordinates(int gameId, int x, int y) throws CluedoException{
        getActiveGame(gameId).getCurrentTurn().setXAndYCoord(x, y);
        cluedoRepository.saveAndFlush(getActiveGame(gameId));
    }

    @Override
    public void finishTurn(int gamedId) throws CluedoException {
        getActiveGame(gamedId).switchTurn();
        refreshGameMessageHandler.sendMessage(gamedId, null, null);
        cluedoRepository.saveAndFlush(getActiveGame(gamedId));
    }

    @Override
    public void validChoice(ChoiceDto choice) throws CluedoException {
        CharacterType type = getCurrentTurn(choice.getGameId()).getPlayer().getCharacterType();
        if(type != choice.getType()) throw new CluedoException(CluedoExceptionType.INVALID_TURN, String.format("It is %s's turn, %s is unable to play", type, choice.getType()));
    }

    @Override
    public void removePlayers(int gameId) throws CluedoException {
        Cluedo cluedo = getCluedoById(gameId);
        for (Player player : cluedo.getPlayers()) {
            cluedo.removePlayer(player);
        }
        cluedoRepository.save(cluedo);
    }


    @Override
    public void replySuggestion(Card card, int gameId, CardDto cardDto) throws CluedoException {
        Cluedo cluedo = getCluedoById(gameId);
        cluedo.getCurrentTurn().setShown(card);
        cluedoRepository.save(cluedo);
        this.suggestionReplyCardHandler.sendMessageId(gameId, cluedo.getCurrentTurn().getPlayer().getPlayerId(), cardDto, null);
    }

    @Override
    public void moveCharacter(List<CardDto> suggestionCards, int gameId,Scene scene) throws CluedoException {
        Cluedo cluedo = getCluedoById(gameId);
        CardDto charachterCard = suggestionCards.stream().filter(card -> card.getCardType() == scene.getCharacterCard().getCardType()).findFirst().get();
        this.boardService.moveCharacter(CharacterType.valueOf(charachterCard.getType().toUpperCase()),
                cluedo.getCurrentTurn().getXCoord(),cluedo.getCurrentTurn().getYCoord(),
                gameId);
    }

    @Override
    public void getAllInfo(Integer cluedoId, Integer playerId) throws CluedoException {
        Cluedo cluedo = getCluedoById(cluedoId);
        cluedo.getPlayers().stream().filter(p -> p.getPlayerId().equals(playerId)).findFirst().get();
    }

    @Override
    public List<Card> getPlayerCards(int gameId, int playerId) throws CluedoException {
        Cluedo cluedo = getCluedoById(gameId);
        Player player = cluedo.getPlayers().stream().filter(p -> playerId == p.getPlayerId()).findFirst().get();
        return player.getCardsInHand();
    }

    @Override
    public Turn getCurrentTurn(int gamedId) throws CluedoException {
        return getCluedoById(gamedId).getCurrentTurn();
    }

    @Override
    public CharacterType getCharacterType(int playerId) throws CluedoException {
        return playerRepository.findByPlayerId(playerId).getCharacterType();
    }
    //#endregion

    //#region HELPERS
    private Cluedo getCluedoById(int cluedoId) throws CluedoException {
        return cluedoRepository.findById(cluedoId)
                .orElseThrow(() -> new CluedoException(CluedoExceptionType.CLUEDO_NOT_FOUND,
                        String.format("Game with id %d not found.", cluedoId)
                ));
    }

    private Cluedo getActiveGame(int gameId) throws CluedoException{
        Optional<Cluedo> optionalCluedo = cluedoRepository.findById(gameId);
        if (optionalCluedo.isEmpty()) throw new CluedoException(CluedoExceptionType.CLUEDO_NOT_FOUND);
        Cluedo cluedo = optionalCluedo.get();
        if (!cluedo.isActive()) throw new CluedoException(CluedoExceptionType.GAME_NOT_ACTIVE);
        return cluedo;
    }
    //#endregion
}

