package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.dto.LocationDto;
import be.kdg.rideservice.repositories.OpenRideRepository;
import be.kdg.rideservice.repositories.RideRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RideServiceTest {
    @MockBean private RideRepository rideRepository;
    @MockBean private OpenRideRepository openRideRepository;

    @Autowired
    private RideService rideService;

    @Test
    public void handleOpenRides() {
        List<Ride> openRides = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            Ride ride = new Ride();
            ride.setRideId(i);
            openRides.add(ride);
        }

        Mockito.when(openRideRepository.getOpenRides()).thenReturn(openRides);
        rideService.handleOpenRides();
    }

    @Test
    public void saveLocation() {
        LocationDto locationDto = new LocationDto(LocalDateTime.now());
        rideService.saveLocation(locationDto);
    }

    @Test(expected = InternalRideServiceException.class)
    public void getOpenRideBySubscriptionNotFound() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        Mockito.when(rideRepository.getRideBySubscriptionAndEndLockIsNull(any())).thenReturn(Optional.empty());
        rideService.getOpenRideBySubscription(subscription);
    }

    @Test
    public void saveRide() {
        Ride ride = new Ride();
        rideService.saveRide(ride);
    }
}
