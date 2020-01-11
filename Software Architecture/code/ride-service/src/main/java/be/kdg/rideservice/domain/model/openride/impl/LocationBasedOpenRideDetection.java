package be.kdg.rideservice.domain.model.openride.impl;

import be.kdg.rideservice.config.OpenRideProperties;
import be.kdg.rideservice.domain.model.openride.OpenRideDetection;
import be.kdg.rideservice.domain.model.ride.RideType;
import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import com.vividsolutions.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Map;

@Component
public class LocationBasedOpenRideDetection implements OpenRideDetection {
    private final OpenRideProperties openRideProperties;

    @Autowired
    public LocationBasedOpenRideDetection(OpenRideProperties openRideProperties) {
        this.openRideProperties = openRideProperties;
    }

    @Override
    public boolean isOpenRide(RideWithLocation rideWithLocation) {
        if (rideWithLocation.getRideType() != RideType.FREE_VEHICLE || rideWithLocation.getPoints().size() == 0) return false;
        Iterator<Map.Entry<LocalDateTime, Point>> itr = rideWithLocation.getPoints().entrySet().iterator();
        Map.Entry<LocalDateTime, Point> previous = rideWithLocation.getPoints().firstEntry();
        Map.Entry<LocalDateTime, Point> next;
        while (itr.hasNext()) {
            next = itr.next();
            boolean isafter = next.getKey().isAfter(previous.getKey());
            //next.getPoint().distance(previous.getPoint()) DOES NOT WORK, returns the distance in degrees of arc.
            boolean hasmoved = calculateDistance(next.getValue().getX(), next.getValue().getY(), previous.getValue().getX(), previous.getValue().getY()) > openRideProperties.getLocationBasedDistanceTreshold();

            if (isafter && hasmoved) {
                previous = next;
            }
        }
        return previous.getKey().plusMinutes(openRideProperties.getLocationBasedTimeTreshold()).isBefore(LocalDateTime.now());
    }


    private double calculateDistance(double x1, double y1, double x2, double y2) {
        double radius = 6378.137;
        double dX = x2 * Math.PI / 180 - x1 * Math.PI / 180;
        double dY = y2 * Math.PI / 180 - y1 * Math.PI / 180;
        double a = Math.pow(Math.sin(dX/2),2) +
                Math.cos(x1 * Math.PI / 180) * Math.cos(x2 * Math.PI / 180) * Math.pow(Math.sin(dY/2),2);
        double c = 2*Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius * c * 1000;
    }
}
