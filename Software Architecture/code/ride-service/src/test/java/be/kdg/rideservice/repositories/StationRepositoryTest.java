package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.station.Station;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StationRepositoryTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private GeometryFactory gf;

    @Test
    @Transactional
    public void checkMapping() {
        Station station = new Station();
        station.setObjectId("abc");
        station.setStationNr("123");
        station.setType("ENKELZIJDIG");
        station.setStreet("Street");
        station.setNumber("123");
        station.setZipCode("2000");
        station.setDistrict("Antwerpen");
        station.setGPSCoord(gf.createPoint(new Coordinate(51, 23)));
        station.setAdditionalInfo("abc");

        Station savedStation = stationRepository.save(station);
        Assert.assertNotNull(savedStation);
        Assert.assertNotNull(savedStation.getStationId());
        Assert.assertEquals(station.getObjectId(), savedStation.getObjectId());
        Assert.assertEquals(station.getStationNr(), savedStation.getStationNr());
        Assert.assertEquals(station.getType(), savedStation.getType());
        Assert.assertEquals(station.getStreet(), savedStation.getStreet());
        Assert.assertEquals(station.getNumber(), savedStation.getNumber());
        Assert.assertEquals(station.getZipCode(), savedStation.getZipCode());
        Assert.assertEquals(station.getGPSCoord(), savedStation.getGPSCoord());
        Assert.assertEquals(station.getAdditionalInfo(), savedStation.getAdditionalInfo());

        stationRepository.delete(savedStation);
    }
}
