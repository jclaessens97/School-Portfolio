package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.dto.LocationDto;

public interface RideService {
    void handleOpenRides();
    void saveLocation(LocationDto locationDto);
    Ride getOpenRideBySubscription(Subscription subscription);
    void saveRide(Ride ride);
}
