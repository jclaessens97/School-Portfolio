package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.users.Player;

import java.util.List;
import java.util.UUID;

public interface LobbyService {
    /**
     * Gets lobby by id
     * @param cluedoId
     * @return
     * @throws CluedoException
     */
    Cluedo getLobbyById(int cluedoId) throws CluedoException;

    /**
     * Gets all lobbies
     * @return
     */
    List<Cluedo> getAllLobbies();

    List<Cluedo> getAllLobbiesNotJoined(UUID userId);
    List<Cluedo> getAllLobbiesJoined(UUID userId);

    /**
     * Create lobby and returns lobbyId
     * @param userId
     * @param characterType
     * @return
     * @throws CluedoException
     */
    int createLobby(UUID userId, CharacterType characterType) throws CluedoException;

    /**
     * Joins open lobby
     * @param cluedoId
     * @param userId
     * @param characterType
     * @throws CluedoException
     */
    void joinLobby(int cluedoId, UUID userId, CharacterType characterType) throws CluedoException;

    /**
     * Leaves lobby
     * @param cluedoId
     * @param playerId
     * @param userId
     * @param isKick
     * @throws CluedoException
     */
    void leaveLobby(int cluedoId, int playerId, UUID userId, boolean isKick) throws CluedoException;

    /**
     * Change lobby settings
     * @param cluedoId
     * @param userId
     * @param lobbyName
     * @param turnDuration
     * @param maxPlayers
     * @throws CluedoException
     */
    void changeGameSettings(int cluedoId, UUID userId, String lobbyName, int turnDuration, int maxPlayers) throws CluedoException;

    /**
     * Starts game if current user is host
     * @param cluedoId
     * @param userId
     * @return
     * @throws CluedoException
     */
    Cluedo startGame(int cluedoId, UUID userId) throws CluedoException;

    /**
     * Gets specific player from the lobby
     * @param cluedo
     * @param userId
     * @return
     * @throws CluedoException
     */
    Player getPlayerInLobby(Cluedo cluedo, UUID userId) throws CluedoException;

    /**
     * Gets the remaining free characters.
     * @param CluedoId
     * @return
     * @throws CluedoException
     */
    List<CharacterType> getFreeCharacters(int CluedoId) throws CluedoException;

    /**
     * Gets a list of the available games to join
     * @param userId
     * @return
     */
    List<Cluedo> getOpenGames(UUID userId);
}
