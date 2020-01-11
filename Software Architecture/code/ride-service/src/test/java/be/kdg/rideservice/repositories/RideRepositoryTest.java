package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RideRepositoryTest {
    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private LockRepository lockRepository;

    @Autowired
    private GeometryFactory gf;

    @Test
    @Transactional
    public void checkMapping() {
        Vehicle vehicle = vehicleRepository.getOne((short) 1);
        Subscription subscription = subscriptionRepository.getOne(1);
        Lock startLock = lockRepository.getOne((short) 1);
        Lock endLock = lockRepository.getOne((short) 2);

        Assert.assertNotNull(vehicle);
        Assert.assertNotNull(subscription);
        Assert.assertNotNull(startLock);
        Assert.assertNotNull(endLock);

        Ride ride = new Ride();
        ride.setStartPoint(gf.createPoint(new Coordinate(51, 52)));
        ride.setEndPoint(gf.createPoint(new Coordinate(52, 53)));
        ride.setStartTime(LocalDateTime.now().minusMinutes(30));
        ride.setEndTime(LocalDateTime.now());
        ride.setVehicle(vehicle);
        ride.setSubscription(subscription);
        ride.setStartLock(startLock);
        ride.setEndLock(endLock);

        Ride savedRide = rideRepository.save(ride);
        Assert.assertNotNull(savedRide);
        Assert.assertNotNull(savedRide.getRideId());
        Assert.assertEquals(ride.getStartPoint(), savedRide.getStartPoint());
        Assert.assertEquals(ride.getEndPoint(), savedRide.getEndPoint());
        Assert.assertEquals(ride.getStartTime(), savedRide.getStartTime());
        Assert.assertEquals(ride.getEndTime(), savedRide.getEndTime());
        Assert.assertEquals(ride.getVehicle(), savedRide.getVehicle());
        Assert.assertEquals(ride.getSubscription(), savedRide.getSubscription());
        Assert.assertEquals(ride.getStartLock(), savedRide.getStartLock());
        Assert.assertEquals(ride.getEndLock(), savedRide.getEndLock());

        rideRepository.delete(savedRide);
    }
}
