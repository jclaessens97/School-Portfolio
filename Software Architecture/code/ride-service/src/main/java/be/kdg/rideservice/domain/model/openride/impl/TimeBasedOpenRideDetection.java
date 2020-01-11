package be.kdg.rideservice.domain.model.openride.impl;

import be.kdg.rideservice.config.OpenRideProperties;
import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.openride.OpenRideDetection;
import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeBasedOpenRideDetection implements OpenRideDetection {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeBasedOpenRideDetection.class);
    private final OpenRideProperties openRideProperties;

    @Autowired
    public TimeBasedOpenRideDetection(OpenRideProperties openRideProperties) {
        this.openRideProperties = openRideProperties;
    }

    @Override
    public boolean isOpenRide(RideWithLocation rideWithLocation) {
        switch (rideWithLocation.getRideType()) {
            case FREE_VEHICLE:
            case STATION_VEHICLE:
                return rideWithLocation.getRide().getStartTime()
                    .plusMinutes(openRideProperties.getTimeBasedTreshold())
                    .isBefore(LocalDateTime.now());
            default:
                throw new InternalRideServiceException("Unknown ridetype detected.");
        }
    }
}
