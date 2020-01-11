package be.kdg.rideservice.unit;

import be.kdg.rideservice.domain.model.openride.impl.LocationBasedOpenRideDetection;
import be.kdg.rideservice.domain.model.openride.impl.TimeBasedOpenRideDetection;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.ride.RideType;
import be.kdg.rideservice.domain.model.ride.RideWithLocation;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.TreeMap;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OpenRideDetectionTest {
    @Autowired
    private LocationBasedOpenRideDetection locationBasedOpenRideDetection;

    @Autowired
    private TimeBasedOpenRideDetection timeBasedOpenRideDetection;

    @Autowired
    private GeometryFactory gf;

    private RideWithLocation rideWithLocation1;
    private RideWithLocation rideWithLocation2;
    private RideWithLocation rideWithLocation3;
    private RideWithLocation rideWithLocation4;

    @Before
    public void setUp() {
        Ride ride1 = new Ride();
        ride1.setStartTime(LocalDateTime.now().minusMinutes(61));
        rideWithLocation1 = new RideWithLocation(new TreeMap<>(), ride1, RideType.STATION_VEHICLE);

        Ride ride2 = new Ride();
        ride2.setStartTime(LocalDateTime.now().minusMinutes(20));
        rideWithLocation2 = new RideWithLocation(new TreeMap<>(), ride2, RideType.STATION_VEHICLE);

        Ride ride3 = new Ride();
        ride3.setStartTime(LocalDateTime.now().minusMinutes(61));
        rideWithLocation3 = new RideWithLocation(new TreeMap<>(), ride3, RideType.FREE_VEHICLE);
        rideWithLocation3.getPoints().put(LocalDateTime.now().minusMinutes(15), gf.createPoint(new Coordinate(4.3324643,51.2194475)));
        rideWithLocation3.getPoints().put(LocalDateTime.now().minusMinutes(7), gf.createPoint(new Coordinate(4.3324643,51.0194475)));

        Ride ride4 = new Ride();
        ride4.setStartTime(LocalDateTime.now().minusMinutes(20));
        rideWithLocation4 = new RideWithLocation(new TreeMap<>(), ride4, RideType.FREE_VEHICLE);
        rideWithLocation4.getPoints().put(LocalDateTime.now().minusMinutes(17), gf.createPoint(new Coordinate(4.4024643,51.2194475)));
        rideWithLocation4.getPoints().put(LocalDateTime.now().minusMinutes(12), gf.createPoint(new Coordinate(4.3924843,51.2326475)));
    }

    @Test
    public void locationBasedOpenRideDetection() {
        Assert.assertFalse(locationBasedOpenRideDetection.isOpenRide(rideWithLocation1));
        Assert.assertFalse(locationBasedOpenRideDetection.isOpenRide(rideWithLocation2));
        Assert.assertFalse(locationBasedOpenRideDetection.isOpenRide(rideWithLocation3));
        Assert.assertTrue(locationBasedOpenRideDetection.isOpenRide(rideWithLocation4));
    }

    @Test
    public void timeBasedOpenRideDetection() {
        Assert.assertTrue(timeBasedOpenRideDetection.isOpenRide(rideWithLocation1));
        Assert.assertFalse(timeBasedOpenRideDetection.isOpenRide(rideWithLocation2));
        Assert.assertTrue(timeBasedOpenRideDetection.isOpenRide(rideWithLocation3));
        Assert.assertFalse(timeBasedOpenRideDetection.isOpenRide(rideWithLocation4));
    }
}
