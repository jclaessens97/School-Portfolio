package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.exceptions.VehicleNotFoundException;
import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.repositories.VehicleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class VehicleServiceTest {
    @MockBean
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    @Test(expected = VehicleNotFoundException.class)
    public void getVehicleByIdNotFound() {
        Mockito.when(vehicleRepository.findById(anyShort())).thenReturn(Optional.empty());
        vehicleService.getVehicleById((short) 1);
    }

    @Test(expected = InternalRideServiceException.class)
    public void findNearestFreeVehicleNotFound() {
        BikeType bikeType = new BikeType();
        bikeType.setBikeTypeId((byte) 1);

        Mockito.when(vehicleRepository.findNearestVehicle(any(), anyByte())).thenReturn(Optional.empty());
        Vehicle vehicle = vehicleService.findNearestFreeVehicle(51.0, 23.0, bikeType);
    }
}
