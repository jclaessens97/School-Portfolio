package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.gameboard.GameBoard;
import be.kdg.cluedobackend.model.gameboard.Room;
import be.kdg.cluedobackend.model.gameboard.Tile;

import java.util.Set;

public interface BoardService {
    /**
     */
    void initialiseBoard();

    /**
     * Retrieves the gameboard state from the database with given gameId.
     * @param gameId
     * @return
     * @throws CluedoException
     */
    GameBoard getBoardByGameId(int gameId) throws CluedoException;

    /**
     * @param boardName
     * @param gameId
     */
    void setupBoard(String boardName, int gameId) throws CluedoException;

    /**
     * @param gameId
     */
    void setupBoard(int gameId) throws CluedoException;

    /**
     * Gets the possible move for a character with number of steps for a given game.
     * @param type
     * @param step
     * @param gameId
     * @return
     * @throws CluedoException
     */
    Set<Tile> getPossibleMoves(CharacterType type, int step, int gameId) throws CluedoException;

    /**
     * Moves character to a given coordinate in a given game.
     * @param type
     * @param newX
     * @param newY
     * @param gameId
     * @return
     * @throws CluedoException
     */
    Tile moveCharacter(CharacterType type, int newX, int newY, int gameId) throws CluedoException;

    /**
     * Moves character to a given room in a given game.
     * @param characterType
     * @param roomType
     * @param gameId
     * @return
     * @throws CluedoException
     */
    Tile moveCharacter(CharacterType characterType, RoomType roomType, int gameId) throws CluedoException;

    /**
     * Gets passage in current room. Returns null if the room doesn't have a passage.
     * @param type
     * @param gameId
     * @return
     */
    Room getPassage(CharacterType type, int gameId);

    /**
     * Moves character via passage for a given game.
     * @param type
     * @param gameId
     * @return
     */
    Room takePassage(CharacterType type, int gameId);
}
