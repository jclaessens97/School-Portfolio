package be.kdg.sensorservice.repositories;

import be.kdg.sensorservice.domain.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Base repository for all Measurements data
 * Uses JpaSpecificationExecutor to easily filter on db-level.
 */
@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long>, JpaSpecificationExecutor<Measurement> {
}
