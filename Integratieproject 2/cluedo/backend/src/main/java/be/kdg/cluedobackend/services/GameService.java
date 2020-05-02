package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.dto.CardDto;
import be.kdg.cluedobackend.dto.ChoiceDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.game.SuggestionType;
import be.kdg.cluedobackend.model.game.Turn;
import be.kdg.cluedobackend.model.users.Player;

import java.util.List;
import java.util.Map;

public interface GameService {
    boolean[] makeAccusation(int gameId, Scene scene, List<CardDto> suggestionCards) throws CluedoException;
    Map<SuggestionType, Player> makeSuggestion(int gameId, Scene scene, List<CardDto> suggestionCards) throws CluedoException;
    Turn getCurrentTurn(int gamedId) throws CluedoException;
    CharacterType getCharacterType(int playerId) throws CluedoException;
    void updateDiceRoll(int gameId, int roll) throws CluedoException;
    void updateChosenCoordinates(int gameId, int x, int y) throws CluedoException;
    void finishTurn(int gamedId) throws CluedoException;
    void validChoice(ChoiceDto choice) throws CluedoException;

    void removePlayers(int gameId) throws CluedoException;

    void replySuggestion(Card card, int gameId, CardDto cardDto) throws CluedoException;

    void moveCharacter(List<CardDto> suggestionCards, int gameId, Scene scene) throws CluedoException;

    void getAllInfo(Integer cluedoId, Integer playerId) throws CluedoException;

    List<Card> getPlayerCards(int gameId, int playerId) throws CluedoException;
}
