package be.kdg.rideservice.domain.model.ride;

import com.vividsolutions.jts.geom.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.TreeMap;

@Getter
@Setter
@AllArgsConstructor
public class RideWithLocation {
    private TreeMap<LocalDateTime, Point> points;
    private Ride ride;
    private RideType rideType;
}
