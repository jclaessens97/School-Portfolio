package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Short> {
}
