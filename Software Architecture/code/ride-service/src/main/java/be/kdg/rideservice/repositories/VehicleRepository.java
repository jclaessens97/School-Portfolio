package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Short> {
    @Query(value = "" +
            "SELECT top(1) v.* FROM Vehicles v " +
            "JOIN BikeLots bl on(v.BikeLotId = bl.BikeLotId) where v.Point IS NOT NULL " +
            "and bl.BikeTypeId = :bikeType " +
            "order by geography\\:\\:STGeomFromText(:point, 4326).STDistance(v.Point.MakeValid().STUnion(v.Point.STStartPoint()).STAsText())",
    nativeQuery = true)
    Optional<Vehicle> findNearestVehicle(@Param("point") String point, @Param("bikeType") byte bikeType);
}
