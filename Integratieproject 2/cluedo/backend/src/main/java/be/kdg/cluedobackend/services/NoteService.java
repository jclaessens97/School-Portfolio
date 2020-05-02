package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.notebook.NotationSymbol;
import be.kdg.cluedobackend.model.notebook.NoteBook;

import java.util.UUID;

public interface NoteService {
    /**
     * Sets lobbyservice to prevent circular DI
     * @param lobbyService
     */
    void setLobbyService(LobbyService lobbyService);

    /**
     * Creates a new notebook based on the number of players
     * @param playerCount
     * @return
     */
    NoteBook initializeNoteBook(int playerCount);

    /**
     * Returns notebook by userId
     * @param userId
     * @param cluedoId
     * @return
     * @throws CluedoException
     */
    NoteBook getNoteBookByUserId(UUID userId, int cluedoId) throws CluedoException;

    /**
     * Saves notebook in the database
     * @param noteBook
     * @return
     */
    NoteBook createNotebook(NoteBook noteBook);

    /**
     * Updates notebook column in the database.
     * @param userId
     * @param notebookId
     * @param cardType
     * @param line
     * @param column
     * @param symbol
     * @throws CluedoException
     */
    void updateNotebookColumn(UUID userId, int notebookId, CardType cardType, int line, int column, NotationSymbol symbol) throws CluedoException;

    /**
     * Updates notebook line in the database.
     * @param userId
     * @param notebookId
     * @param cardType
     * @param line
     * @param crossed
     * @throws CluedoException
     */
    void updateNoteBookLine(UUID userId, int notebookId, CardType cardType, int line, boolean crossed) throws CluedoException;
}
