package be.kdg.rideservice.domain.model.openride;

import be.kdg.rideservice.domain.model.ride.RideWithLocation;

public interface OpenRideDetection {
    public boolean isOpenRide(RideWithLocation rideWithLocation);
}
