package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.repositories.StationRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyShort;

@SpringBootTest
@RunWith(SpringRunner.class)
public class StationServiceTest {
    @MockBean private StationRepository stationRepository;

    @Autowired
    private StationService stationService;

    @Test(expected = InternalRideServiceException.class)
    public void getStationByStationIdNotFound() {
        Mockito.when(stationRepository.findById(anyShort())).thenReturn(Optional.empty());
        stationService.getStationByStationId((short) 1);
    }
}
