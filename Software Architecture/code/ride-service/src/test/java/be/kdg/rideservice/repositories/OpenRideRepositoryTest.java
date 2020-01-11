package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import be.kdg.rideservice.domain.model.vehicle.BikeLot;
import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.LocationDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OpenRideRepositoryTest {
    @Autowired
    private OpenRideRepository openRideRepository;

    @Autowired
    private Map<Short, RideWithLocation> currentRidesWithLocation;

    @After
    public void tearDown() throws Exception {
        currentRidesWithLocation.clear();
    }

    @Test
    @Transactional
    public void getOpenRides() {
        BikeType bikeType1 = new BikeType();
        bikeType1.setBikeTypeId((byte)1);
        BikeLot bikeLot1 = new BikeLot();
        bikeLot1.setBikeType(bikeType1);
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setBikeLot(bikeLot1);
        vehicle1.setVehicleId((short)1);
        Ride ride1 = new Ride();
        ride1.setVehicle(vehicle1);
        ride1.setStartTime(LocalDateTime.now().minusMinutes(61));

        BikeType bikeType2 = new BikeType();
        bikeType2.setBikeTypeId((byte)1);
        BikeLot bikeLot2 = new BikeLot();
        bikeLot2.setBikeType(bikeType2);
        Vehicle vehicle2 = new Vehicle();
        vehicle2.setBikeLot(bikeLot2);
        vehicle2.setVehicleId((short)2);
        Ride ride2 = new Ride();
        ride2.setVehicle(vehicle2);
        ride2.setStartTime(LocalDateTime.now().minusMinutes(20));

        openRideRepository.startRide(ride1);
        openRideRepository.startRide(ride2);

        Assert.assertEquals(1, openRideRepository.getOpenRides().size());
        Assert.assertTrue(openRideRepository.getOpenRides().contains(ride1));
        Assert.assertFalse(openRideRepository.getOpenRides().contains(ride2));
    }

    @Test
    @Transactional
    public void saveRideLocation() {
        BikeType bikeType = new BikeType();
        bikeType.setBikeTypeId((byte)3);
        BikeLot bikeLot = new BikeLot();
        bikeLot.setBikeType(bikeType);
        Vehicle vehicle = new Vehicle();
        vehicle.setBikeLot(bikeLot);
        vehicle.setVehicleId((short) 1);
        Ride ride = new Ride();
        ride.setVehicle(vehicle);
        ride.setStartTime(LocalDateTime.now().minusMinutes(20));


        LocationDto locationDto = new LocationDto(LocalDateTime.now().minusMinutes(11));
        locationDto.setVehicleId((short) 1);
        locationDto.setXCoord(4.4024643);
        locationDto.setYCoord(51.2194475);

        openRideRepository.startRide(ride);
        openRideRepository.saveRideLocation(locationDto);

        Assert.assertTrue(currentRidesWithLocation.containsKey((short) 1));
    }

    @Test(expected = InternalRideServiceException.class)
    @Transactional
    public void saveRideLocationOfInexistingRide() {
        LocationDto locationDto = new LocationDto(LocalDateTime.now());
        locationDto.setVehicleId((short) 1);
        openRideRepository.saveRideLocation(locationDto);
    }

    @Test
    @Transactional
    public void startRide() {
        BikeType bikeType1 = new BikeType();
        bikeType1.setBikeTypeId((byte) 1);

        BikeLot bikeLot1 = new BikeLot();
        bikeLot1.setBikeType(bikeType1);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setBikeLot(bikeLot1);
        vehicle1.setVehicleId((short) 1);

        Ride ride = new Ride();
        ride.setVehicle(vehicle1);
        ride.setStartTime(LocalDateTime.now());

        openRideRepository.startRide(ride);

        Assert.assertEquals(currentRidesWithLocation.size(), 1);
    }

    @Test
    @Transactional
    public void endRide() {
        BikeType bikeType1 = new BikeType();
        bikeType1.setBikeTypeId((byte) 1);

        BikeLot bikeLot1 = new BikeLot();
        bikeLot1.setBikeType(bikeType1);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setBikeLot(bikeLot1);
        vehicle1.setVehicleId((short) 1);

        Ride ride = new Ride();
        ride.setVehicle(vehicle1);
        ride.setStartTime(LocalDateTime.now());

        openRideRepository.startRide(ride);
        Assert.assertEquals(currentRidesWithLocation.size(), 1);
        openRideRepository.endRide(ride);
        Assert.assertEquals(currentRidesWithLocation.size(), 0);
    }
}
