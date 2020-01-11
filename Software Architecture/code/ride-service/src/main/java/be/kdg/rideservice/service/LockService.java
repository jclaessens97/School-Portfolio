package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.station.Station;

import java.util.List;

public interface LockService {
    List<Short> getFreeLockIdsByStationId(short stationId);
    Lock unlockStationVehicle(int userId, short stationId);
    void unlockFreeVehicle(int userId, short vehicleId);
    void lockStationVehicle(int userId, short lockId);
    void lockFreeVehicle(int userId, short vehicleId);

    Lock getLockById(short lockId);
    Lock getRandomLockAtStation(Station station);
}
