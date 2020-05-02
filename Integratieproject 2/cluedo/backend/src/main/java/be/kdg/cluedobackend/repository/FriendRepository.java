package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.users.Friend;
import be.kdg.cluedobackend.model.users.FriendType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByAsking_UserNameAndResponding_UserId(String userName, UUID userId);
    Optional<Friend> findByAsking_UserIdAndResponding_UserName(UUID userId, String friendUserName);
    List<Friend> findAllByAsking_UserIdAndFriendTypeOrResponding_UserIdAndFriendType(UUID userId1, FriendType friendType1, UUID userId2, FriendType friendType2);
    List<Friend> findAllByResponding_UserIdAndFriendType(UUID userId, FriendType friendType);
}
