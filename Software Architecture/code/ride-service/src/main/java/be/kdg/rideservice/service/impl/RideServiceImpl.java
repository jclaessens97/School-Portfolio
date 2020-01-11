package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.dto.LocationDto;
import be.kdg.rideservice.repositories.OpenRideRepository;
import be.kdg.rideservice.repositories.RideRepository;
import be.kdg.rideservice.service.RideService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RideServiceImpl implements RideService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RideServiceImpl.class);
    private final RideRepository rideRepository;
    private final OpenRideRepository openRideRepository;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository, OpenRideRepository openRideRepository) {
        this.rideRepository = rideRepository;
        this.openRideRepository = openRideRepository;
    }

    //Check for open rides every 5 seconds and log the rideId
    @Scheduled(fixedDelay = 5000)
    public void handleOpenRides() {
        for (Ride ride : openRideRepository.getOpenRides()) {
            LOGGER.info("Open ride detected for ride with id: " + ride.getRideId());
            openRideRepository.endRide(ride);
        }
    }

    @Override
    public void saveLocation(LocationDto locationDto) {
        openRideRepository.saveRideLocation(locationDto);
    }

    @Override
    public Ride getOpenRideBySubscription(Subscription subscription) {
        return rideRepository
            .getRideBySubscriptionAndEndLockIsNull(subscription)
            .orElseThrow(() -> new InternalRideServiceException(
                String.format(
                    "No open ride found for subscription with subscriptionId %d",
                    subscription.getSubscriptionId()
                )
            ));
    }

    @Override
    public void saveRide(Ride ride) {
        rideRepository.save(ride);
        openRideRepository.startRide(ride);
    }
}
