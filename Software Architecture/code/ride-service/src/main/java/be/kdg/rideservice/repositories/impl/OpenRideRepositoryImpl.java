package be.kdg.rideservice.repositories.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.openride.OpenRideDetection;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.ride.RideType;
import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import be.kdg.rideservice.dto.LocationDto;
import be.kdg.rideservice.repositories.OpenRideRepository;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Repository
public class OpenRideRepositoryImpl implements OpenRideRepository {
    private final Map<Short, RideWithLocation> currentRidesWithLocation;
    private final List<OpenRideDetection> openRideDetections;
    private final GeometryFactory gf;

    @Autowired
    public OpenRideRepositoryImpl(Map<Short, RideWithLocation> currentRidesWithLocation, List<OpenRideDetection> openRideDetections, GeometryFactory gf) {
        this.currentRidesWithLocation = currentRidesWithLocation;
        this.openRideDetections = openRideDetections;
        this.gf = gf;
    }

    /**
     * Loops through list of current rides and matches them with all available openride detection algorithms.
     * When an openride is detected, add it to a list and returns the list of open rides.
     */
    @Override
    public List<Ride> getOpenRides() {
        return currentRidesWithLocation
                .values()
                .stream()
                .filter(r -> {
                    for (OpenRideDetection detection : openRideDetections) {
                        if (detection.isOpenRide(r)) {
                            return true;
                        }
                    }

                    return false;
                })
                .map(RideWithLocation::getRide)
                .collect(Collectors.toList());
    }

    @Override
    public void saveRideLocation(LocationDto locationDto) {
        final short id = locationDto.getVehicleId();

        if (currentRidesWithLocation.containsKey(id)) {
            currentRidesWithLocation.get(id).getPoints()
                    .put(locationDto.getTimeStamp(), gf.createPoint(new Coordinate(locationDto.getXCoord(), locationDto.getYCoord())));

        } else {
            throw new InternalRideServiceException(String.format("Could not find vehicle with id %d and thus could not save its location", id));
        }
    }

    @Override
    public void startRide(Ride ride) {
        RideType rideType;
        switch (ride.getVehicle().getBikeLot().getBikeType().getBikeTypeId()) {
            case 1:
            case 2:
                rideType = RideType.STATION_VEHICLE;
                break;
            case 3:
            case 4:
                rideType = RideType.FREE_VEHICLE;
                break;
            default:
                throw new InternalRideServiceException(String.format("Could not assign ride_type to ride %d", ride.getRideId()));
        }

        currentRidesWithLocation.put(ride.getVehicle().getVehicleId(), new RideWithLocation(new TreeMap<>(),ride, rideType));
    }

    @Override
    public void endRide(Ride ride) {
        currentRidesWithLocation.remove(ride.getVehicle().getVehicleId());
    }
}
