package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.users.Player;

import java.util.List;

public interface CardService {
    /**
     * Creates solution Scene
     * @return
     * @throws CluedoException
     */
    Scene createSolutionScene() throws CluedoException;

    /**
     * Deals all cards according to the caseFile and the (number of) joined players.
     * @param joinedPlayers
     * @param caseFile
     */
    void dealCardsToPlayers(List<Player> joinedPlayers, Scene caseFile);

    /**
     * Initializes default card deck if not already initialized.
     */
    void initializeDefault();

    /**
     * Retrieves card by id
     * @param id
     * @return
     */
    Card getCardById(int id) throws CluedoException;

    /**
     * Gets all cards from the database
     * @return
     */
    List<Card> getAllCards();
}
