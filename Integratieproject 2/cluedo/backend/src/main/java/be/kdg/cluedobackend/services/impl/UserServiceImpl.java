package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.controllers.messagehandlers.InviteMessageHandler;
import be.kdg.cluedobackend.controllers.messagehandlers.MessageHandler;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.users.*;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.FriendRepository;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import be.kdg.cluedobackend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final CluedoRepository cluedoRepository;
    private final InviteMessageHandler inviteMessageHandler;
    private final PlayerRepository playerRepository;
    private final MessageHandler<String> friendRequestHandler;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, FriendRepository friendRepository, CluedoRepository cluedoRepository,
                           InviteMessageHandler inviteMessageHandler, PlayerRepository playerRepository, @Qualifier("friendRequestMessageHandler") MessageHandler<String> friendRequestHandler) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
        this.playerRepository = playerRepository;
        this.cluedoRepository = cluedoRepository;
        this.inviteMessageHandler = inviteMessageHandler;
        this.friendRequestHandler = friendRequestHandler;
    }

    //#region CRUD
    @Override
    public User syncUser(UUID userId, String userName, List<Role> roles) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            User user = new User(userId, userName, roles);
            return userRepository.save(user);
        }

        User user = userRepository.getOne(userId);
        if (!user.getUserName().equals(userName)) {
            user.setUserName(userName);
            return userRepository.save(user);
        }

        return user;
    }

    @Override
    public User getUserById(UUID userId) throws CluedoException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CluedoException(CluedoExceptionType.USER_NOT_FOUND));
    }
    @Override
    public Boolean addFriend(UUID userId, String friendUserName) throws CluedoException {
        Optional<User> asking = userRepository.findById(userId);
        Optional<User> responding = userRepository.findByUserName(friendUserName);
        if (asking.isEmpty() || responding.isEmpty()) throw new CluedoException(CluedoExceptionType.USER_NOT_FOUND);
        if (asking.get().equals(responding.get())) throw new CluedoException(CluedoExceptionType.INVALID_ADD_FRIEND);
        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserNameAndResponding_UserId(friendUserName, userId);
        if (optionalFriend.isPresent()) throw new CluedoException(CluedoExceptionType.INVALID_ADD_FRIEND);
        optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(userId, friendUserName);
        if (optionalFriend.isPresent()) throw new CluedoException(CluedoExceptionType.INVALID_ADD_FRIEND);
        Friend friend = new Friend(asking.get(), responding.get());
        friendRepository.save(friend);
        friendRequestHandler.sendMessage(null, friendUserName, null);
        return true;
    }

    @Override
    public Boolean updateFriend(UUID userId, String friendUserName, FriendType friendType) throws CluedoException {
        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserNameAndResponding_UserId(friendUserName, userId);
        if (optionalFriend.isEmpty()) {
            optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(userId, friendUserName);
            if (optionalFriend.isEmpty()) throw new CluedoException(CluedoExceptionType.FRIEND_NOT_FOUND);
        }
        Friend friend = optionalFriend.get();
        if (friendType == FriendType.DELETE_PENDING){
            friendRepository.delete(friend);
        } else {
            friend.setFriendType(friendType);
            friendRepository.save(friend);
        }
        friendRequestHandler.sendMessage(null, friendUserName, null);
        return true;
    }

    @Override
    public List<User> getFriends(UUID userId, FriendType friendType) {
        List<Friend> friends = friendRepository.findAllByAsking_UserIdAndFriendTypeOrResponding_UserIdAndFriendType(userId, friendType, userId, friendType);
        return friends.stream().map(f -> {
            if (f.getAsking().getUserId().equals(userId)) return f.getResponding();
            if (f.getResponding().getUserId().equals(userId)) return f.getAsking();
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableFriends(UUID userId, int cluedoId) throws CluedoException {
        List<Friend> friends = friendRepository.findAllByAsking_UserIdAndFriendTypeOrResponding_UserIdAndFriendType(userId, FriendType.CONFIRMED, userId, FriendType.CONFIRMED);
        Optional<Cluedo> optionalCluedo = cluedoRepository.findById(cluedoId);
        if (optionalCluedo.isEmpty()) throw new CluedoException(CluedoExceptionType.CLUEDO_NOT_FOUND);
        Cluedo cluedo = optionalCluedo.get();
        if (cluedo.isActive()) throw new CluedoException(CluedoExceptionType.GAME_ALREADY_STARTED);
        return friends.stream()
                .map(f -> {
                    if (f.getAsking().getUserId().equals(userId)) return f.getResponding();
                    if (f.getResponding().getUserId().equals(userId)) return f.getAsking();
                    return null;
                })
                .filter(f -> !cluedo.getPlayers().stream().map(Player::getUser).collect(Collectors.toList()).contains(f))
                .filter(Objects::nonNull)
                .map(User::getUserName)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getPendings(UUID userId) {
        return friendRepository.findAllByResponding_UserIdAndFriendType(userId, FriendType.PENDING)
                .stream().map(Friend::getAsking).collect(Collectors.toList());
    }

    @Override
    public GameStatistics getStatistics(int cluedoId, int playerId) throws CluedoException {
        Player player = playerRepository.findByCluedo_CluedoIdAndPlayerId(cluedoId, playerId);
        if (player != null) {
            return player.getUser().getGameStatistics();
        }
        throw new CluedoException(CluedoExceptionType.USER_NOT_FOUND);
    }

    @Override
    public GameStatistics getStatistics(String userName) throws CluedoException {
        if (userRepository.findByUserName(userName).isPresent()) {
            return userRepository.findByUserName(userName).get().getGameStatistics();
        }
        throw new CluedoException(CluedoExceptionType.USER_NOT_FOUND);
    }

    @Override
    public Boolean deleteFriend(UUID userId, String friendUserName) throws CluedoException {
        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserNameAndResponding_UserId(friendUserName, userId);
        if (optionalFriend.isEmpty()) {
            optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(userId, friendUserName);
            if (optionalFriend.isEmpty()) throw new CluedoException(CluedoExceptionType.FRIEND_NOT_FOUND);
        }
        Friend friend = optionalFriend.get();
        friendRepository.delete(friend);
        friendRequestHandler.sendMessage(null,friendUserName,null);
        return true;
    }

    @Override
    public void invite(UUID userId, int cluedoId, String userName) throws CluedoException {
        Optional<User> optionalAsking = userRepository.findById(userId);
        if (optionalAsking.isEmpty()) throw new CluedoException(CluedoExceptionType.USER_NOT_FOUND);
        String askingUserName = optionalAsking.get().getUserName();
        Optional<User> optionalInvited = userRepository.findByUserName(userName);
        if (optionalInvited.isEmpty()) throw new CluedoException(CluedoExceptionType.USER_NOT_FOUND);
        Optional<Cluedo> optionalCluedo = cluedoRepository.findById(cluedoId);
        if (optionalCluedo.isEmpty()) throw new CluedoException(CluedoExceptionType.CLUEDO_NOT_FOUND);
        if (optionalCluedo.get().isActive()) throw new CluedoException(CluedoExceptionType.GAME_ALREADY_STARTED);
        inviteMessageHandler.sendMessage(cluedoId, userName, askingUserName);
    }

    //#endregion
}
