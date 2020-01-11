package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import com.vividsolutions.jts.geom.Point;

import java.util.List;

public interface VehicleService {
    public void saveLocation(Point point, Short vehicleId);
    public Vehicle findNearestFreeVehicle(double xCoord, double yCoord, BikeType type);

    // CRUD API
    public List<Vehicle> getAllVehicles();
    public Vehicle getVehicleById(short id);
    public Vehicle addVehicle(Vehicle vehicle);
    public void updateVehicle(Vehicle vehicle);
    public void deleteVehicleById(short id);
}
