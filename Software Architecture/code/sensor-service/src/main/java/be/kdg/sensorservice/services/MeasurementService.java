package be.kdg.sensorservice.services;

import be.kdg.sensorservice.domain.model.Filter;
import be.kdg.sensorservice.domain.model.Measurement;

import java.util.List;

/**
 * Delegates methods to create/read the measurements
 */
public interface MeasurementService {
    public List<Measurement> getMeasurements(Filter filter);
    public Measurement getMeasurementById(long id);
    public void saveMeasurement(Measurement measurement);
}
