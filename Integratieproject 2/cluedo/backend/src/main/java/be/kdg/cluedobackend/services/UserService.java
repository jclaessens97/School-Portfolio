package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.users.FriendType;
import be.kdg.cluedobackend.model.users.GameStatistics;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User getUserById(UUID userId) throws CluedoException;
    User syncUser(UUID userId, String userName, List<Role> roles);
    Boolean addFriend(UUID userId, String friendUserName) throws CluedoException;
    Boolean deleteFriend(UUID userId, String friendUserName) throws CluedoException;
    Boolean updateFriend(UUID userId, String friendUserName, FriendType friendType) throws CluedoException;
    List<User> getFriends(UUID userId, FriendType friendType);
    List<User> getPendings(UUID userId);

    GameStatistics getStatistics(int cluedoId, int playerId) throws CluedoException;
    GameStatistics getStatistics(String userName) throws CluedoException;
    List<String> getAvailableFriends(UUID userId, int cluedoId) throws CluedoException;
    void invite(UUID userId, int cluedoId, String userName) throws CluedoException;
}
