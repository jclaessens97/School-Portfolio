package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.station.Station;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.repositories.LockRepository;
import be.kdg.rideservice.repositories.StationRepository;
import be.kdg.rideservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LockServiceImpl implements LockService {
    private final LockRepository lockRepository;
    private final SubscriptionService subscriptionService;
    private final VehicleService vehicleService;
    private final StationService stationService;
    private final RideService rideService;

    @Autowired
    public LockServiceImpl(
        LockRepository lockRepository,
        StationRepository stationRepository,
        SubscriptionService subscriptionService,
        VehicleService vehicleService,
        StationService stationService,
        RideService rideService
    ) {
        this.lockRepository = lockRepository;
        this.vehicleService = vehicleService;
        this.subscriptionService = subscriptionService;
        this.stationService = stationService;
        this.rideService = rideService;
    }

    @Override
    public List<Short> getFreeLockIdsByStationId(short stationId) {
        List<Lock> freeLocks = lockRepository.findLocksByStation_StationIdAndVehicleIsNull(stationId);
        return freeLocks
            .stream()
            .map(Lock::getLockId)
            .collect(Collectors.toList());
    }

    @Override
    public Lock unlockStationVehicle(int userId, short stationId) {
        final Subscription subscription = subscriptionService.getSubscriptionByUserId(userId);
        final Station station = stationService.getStationByStationId(stationId);
        final Lock selectedLock = getRandomLockAtStation(station);

        Ride ride = new Ride();
        ride.setStartPoint(selectedLock.getStation().getGPSCoord());
        ride.setStartTime(LocalDateTime.now());
        ride.setVehicle(selectedLock.getVehicle());
        ride.setSubscription(subscription);
        ride.setStartLock(selectedLock);

        selectedLock.setVehicle(null);
        lockRepository.save(selectedLock);
        rideService.saveRide(ride);

        return selectedLock;
    }

    @Override
    public void unlockFreeVehicle(int userId, short vehicleId) {
        final Subscription subscription = subscriptionService.getSubscriptionByUserId(userId);
        final Vehicle vehicle = vehicleService.getVehicleById(vehicleId);

        Ride ride = new Ride();
        ride.setStartPoint(vehicle.getPoint());
        ride.setStartTime(LocalDateTime.now());
        ride.setSubscription(subscription);

        rideService.saveRide(ride);
    }

    @Override
    public void lockStationVehicle(int userId, short lockId) {
        final Subscription subscription = subscriptionService.getSubscriptionByUserId(userId);
        final Ride ride = rideService.getOpenRideBySubscription(subscription);
        final Lock selectedLock = getLockById(lockId);

        selectedLock.setVehicle(ride.getVehicle());
        lockRepository.save(selectedLock);
        ride.setEndPoint(selectedLock.getStation().getGPSCoord());
        ride.setEndTime(LocalDateTime.now());
        ride.setEndLock(selectedLock);
        rideService.saveRide(ride);
    }

    @Override
    public void lockFreeVehicle(int userId, short vehicleId) {
        final Subscription subscription = subscriptionService.getSubscriptionByUserId(userId);
        final Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        final Ride ride = rideService.getOpenRideBySubscription(subscription);

        ride.setEndPoint(vehicle.getPoint());
        ride.setEndTime(LocalDateTime.now());
        rideService.saveRide(ride);
    }

    @Override
    public Lock getLockById(short lockId) {
        return lockRepository
            .findById(lockId)
            .orElseThrow(() -> new InternalRideServiceException(
                String.format("No lock with lockId %d", lockId))
            );
    }

    @Override
    public Lock getRandomLockAtStation(Station station) {
        return lockRepository
            .findFirstByStation_StationIdAndVehicleIsNotNull(station.getStationId())
            .orElseThrow(() -> new InternalRideServiceException("No vehicle available at this station"));
    }
}
