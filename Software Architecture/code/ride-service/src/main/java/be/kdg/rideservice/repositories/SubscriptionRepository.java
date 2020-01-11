package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(int userId);
}
