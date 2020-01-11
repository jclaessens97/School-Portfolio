package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> getRideBySubscriptionAndEndLockIsNull(Subscription subscription);
}
