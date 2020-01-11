package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.exceptions.VehicleNotFoundException;
import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.repositories.VehicleRepository;
import be.kdg.rideservice.service.VehicleService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class VehicleServiceImpl implements VehicleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleServiceImpl.class);
    private final VehicleRepository vehicleRepository;
    private final GeometryFactory geometryFactory;

    @Autowired
    public VehicleServiceImpl(VehicleRepository vehicleRepository, GeometryFactory geometryFactory) {
        this.vehicleRepository = vehicleRepository;
        this.geometryFactory = geometryFactory;
    }

    //#region CRUD API
    @Override
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Override
    public Vehicle getVehicleById(short id) {
        return vehicleRepository
                .findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(
                        String.format("Vehicle with ID %d not found", id)
                ));
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        Vehicle createdVehicle = vehicleRepository.save(vehicle);
        LOGGER.info("Created new vehicle.");
        return createdVehicle;
    }

    @Override
    public void updateVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
        LOGGER.info(String.format("Updated vehicle with ID %d", vehicle.getVehicleId()));
    }

    @Override
    public void deleteVehicleById(short id) {
        Vehicle vehicle = this.getVehicleById(id);
        vehicleRepository.delete(vehicle);
        LOGGER.info(String.format("Vehicle with ID %d has been deleted.", id));
    }
    //#endregion

    @Override
    public void saveLocation(Point point, Short vehicleId) {
        Vehicle vehicle = this.getVehicleById(vehicleId);

        vehicle.setPoint(point);
        vehicleRepository.save(vehicle);
        LOGGER.info(String.format("Location updated of vehicle: %d",vehicle.getVehicleId()));
    }

    @Override
    public Vehicle findNearestFreeVehicle(double xCoord, double yCoord, BikeType type) {
        Point myPoint = geometryFactory.createPoint(new Coordinate(xCoord, yCoord));
        return vehicleRepository.findNearestVehicle(myPoint.toString(), type.getBikeTypeId())
            .orElseThrow(() -> new InternalRideServiceException("Could not retrieve nearest vehicle at this moment."));
    }
}
