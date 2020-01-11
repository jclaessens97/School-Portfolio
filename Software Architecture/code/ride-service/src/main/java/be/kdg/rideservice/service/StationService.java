package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.station.Station;

public interface StationService {
    Station getStationByStationId(short stationId);
}
