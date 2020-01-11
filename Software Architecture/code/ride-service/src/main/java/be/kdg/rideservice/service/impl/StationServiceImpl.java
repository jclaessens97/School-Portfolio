package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.station.Station;
import be.kdg.rideservice.repositories.StationRepository;
import be.kdg.rideservice.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StationServiceImpl implements StationService {
    private final StationRepository stationRepository;

    @Autowired
    public StationServiceImpl(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public Station getStationByStationId(short stationId) {
        return stationRepository
            .findById(stationId)
            .orElseThrow(() -> new InternalRideServiceException(
                    String.format("Station with ID %d does not exist.", stationId))
            );
    }
}
