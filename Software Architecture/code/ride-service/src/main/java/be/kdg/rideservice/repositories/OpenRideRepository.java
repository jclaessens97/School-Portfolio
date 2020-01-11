package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.dto.LocationDto;

import java.util.List;

public interface OpenRideRepository {
    public List<Ride> getOpenRides();
    public void saveRideLocation(LocationDto locationDto);
    void startRide(Ride ride);
    void endRide(Ride ride);
}
