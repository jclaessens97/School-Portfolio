package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.station.Station;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LockRepositoryTest {
    @Autowired
    private LockRepository lockRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Test
    @Transactional
    public void checkMapping() {
        Station station = stationRepository.getOne((short) 1);
        Vehicle vehicle = vehicleRepository.getOne((short) 1);

        Assert.assertNotNull(station);
        Assert.assertNotNull(vehicle);

        Lock lock = new Lock();
        lock.setLockId((short)3);
        lock.setStationLockNr((byte) 1);
        lock.setStation(station);
        lock.setVehicle(vehicle);

        Lock savedLock = lockRepository.save(lock);
        Assert.assertNotNull(savedLock);
        Assert.assertNotNull(savedLock.getLockId());
        Assert.assertEquals(lock.getStationLockNr(), savedLock.getStationLockNr());
        Assert.assertEquals(lock.getStation(), savedLock.getStation());
        Assert.assertEquals(lock.getVehicle(), savedLock.getVehicle());

        lockRepository.delete(lock);
    }
}
