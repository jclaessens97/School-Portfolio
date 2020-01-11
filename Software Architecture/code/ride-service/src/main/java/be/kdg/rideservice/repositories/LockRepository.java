package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.station.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockRepository extends JpaRepository<Lock, Short> {
    List<Lock> findLocksByStation_StationIdAndVehicleIsNull(short stationId);
    Optional<Lock> findFirstByStation_StationIdAndVehicleIsNotNull(short stationId);
}
