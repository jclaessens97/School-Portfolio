package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.vehicle.BikeLot;
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

@SpringBootTest
@RunWith(SpringRunner.class)
public class VehicleRepositoryTest {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LockRepository lockRepository;

    @Autowired
    private GeometryFactory gf;

    @Test
    @Transactional
    public void checkMapping() {
        BikeLot bikeLot = new BikeLot();
        bikeLot.setBikeLotId((short) 1);

        Lock lock = lockRepository.getOne((short) 1);

        Vehicle vehicle = new Vehicle();
        vehicle.setSerialNumber("abc");
        vehicle.setBikeLot(bikeLot);
        vehicle.setLock(lock);
        vehicle.setPoint(gf.createPoint(new Coordinate(51, 52)));

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        Assert.assertNotNull(savedVehicle);
        Assert.assertNotNull(savedVehicle.getVehicleId());
        Assert.assertEquals(vehicle.getSerialNumber(), savedVehicle.getSerialNumber());
        Assert.assertEquals(vehicle.getBikeLot(), savedVehicle.getBikeLot());
        Assert.assertEquals(vehicle.getLock(), savedVehicle.getLock());
        Assert.assertEquals(vehicle.getPoint(), savedVehicle.getPoint());

        vehicleRepository.delete(savedVehicle);
    }
}
