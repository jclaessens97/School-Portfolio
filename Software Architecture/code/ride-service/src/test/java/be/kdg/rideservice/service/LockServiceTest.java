package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.station.Station;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.repositories.*;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LockServiceTest {
    @MockBean private LockRepository lockRepository;
    @MockBean private SubscriptionRepository subscriptionRepository;
    @MockBean private StationRepository stationRepository;
    @MockBean private VehicleRepository vehicleRepository;
    @MockBean private RideRepository rideRepository;
    @MockBean private OpenRideRepository openRideRepository;

    @Autowired
    private LockService lockService;

    @Autowired
    private GeometryFactory geometryFactory;

    @Test
    public void getFreeLocksByStationId() {
        List<Lock> freeLocks = new ArrayList<>();
        for (short i = 1; i <= 5; i++) {
            Lock lock = new Lock();
            lock.setLockId(i);
            freeLocks.add(lock);
        }

        Mockito.when(lockRepository.findLocksByStation_StationIdAndVehicleIsNull(anyShort())).thenReturn(freeLocks);

        List<Short> validationFreeLockIds = freeLocks.stream().map(Lock::getLockId).collect(Collectors.toList());
        List<Short> freeLockIds = lockService.getFreeLockIdsByStationId((short) 1);

        Assert.assertEquals(validationFreeLockIds, freeLockIds);
    }

    @Test
    public void unlockStationVehicle() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        Station station = new Station();
        station.setStationId((short) 1);
        station.setGPSCoord(geometryFactory.createPoint(new Coordinate(51, 52)));

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId((short) 1);

        Lock lock = new Lock();
        lock.setLockId((short) 1);
        lock.setStation(station);
        lock.setVehicle(vehicle);

        Mockito.when(subscriptionRepository.findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(anyInt())).thenReturn(Optional.of(subscription));
        Mockito.when(stationRepository.findById(anyShort())).thenReturn(Optional.of(station));
        Mockito.when(lockRepository.findFirstByStation_StationIdAndVehicleIsNotNull(anyShort())).thenReturn(Optional.of(lock));

        Lock selectedLock = lockService.unlockStationVehicle(1, (short) 1);

        Assert.assertEquals(lock.getLockId(), selectedLock.getLockId());
        Assert.assertNull(lock.getVehicle());
    }

    @Test
    public void unlockFreeVehicle() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId((short) 1);

        Mockito.when(subscriptionRepository.findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(anyInt())).thenReturn(Optional.of(subscription));
        Mockito.when(vehicleRepository.findById(anyShort())).thenReturn(Optional.of(vehicle));

        lockService.unlockFreeVehicle(1, (short) 1);
    }

    @Test
    public void lockStationVehicle() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        Ride ride = new Ride();
        ride.setRideId(1L);

        Station station = new Station();
        station.setStationId((short) 1);
        station.setGPSCoord(geometryFactory.createPoint(new Coordinate(51, 52)));

        Lock lock = new Lock();
        lock.setLockId((short) 1);
        lock.setStation(station);

        Mockito.when(subscriptionRepository.findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(anyInt())).thenReturn(Optional.of(subscription));
        Mockito.when(rideRepository.getRideBySubscriptionAndEndLockIsNull(any())).thenReturn(Optional.of(ride));
        Mockito.when(lockRepository.findById(anyShort())).thenReturn(Optional.of(lock));

        lockService.lockStationVehicle(1, (short) 1);
    }

    @Test
    public void lockFreeVehicle() {
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(1);

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId((short) 1);

        Ride ride = new Ride();
        ride.setRideId(1L);

        Mockito.when(subscriptionRepository.findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(anyInt())).thenReturn(Optional.of(subscription));
        Mockito.when(vehicleRepository.findById(anyShort())).thenReturn(Optional.of(vehicle));
        Mockito.when(rideRepository.getRideBySubscriptionAndEndLockIsNull(any())).thenReturn(Optional.of(ride));

        lockService.lockFreeVehicle(1, (short) 1);
    }

    @Test(expected = InternalRideServiceException.class)
    public void getLockByIdNotFound() {
        Mockito.when(lockRepository.findById(anyShort())).thenReturn(Optional.empty());
        lockService.getLockById((short) 1);
    }

    @Test(expected = InternalRideServiceException.class)
    public void getRandomLockAtStationNotFound() {
        Station station = new Station();
        station.setStationId((short) 1);

        Mockito.when(lockRepository.findFirstByStation_StationIdAndVehicleIsNotNull(anyShort())).thenReturn(Optional.empty());
        lockService.getRandomLockAtStation(station);
    }
}
