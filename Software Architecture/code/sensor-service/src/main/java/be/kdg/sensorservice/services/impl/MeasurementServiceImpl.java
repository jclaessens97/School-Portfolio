package be.kdg.sensorservice.services.impl;

import be.kdg.sensorservice.domain.exceptions.InternalSensorServiceException;
import be.kdg.sensorservice.domain.model.Filter;
import be.kdg.sensorservice.domain.model.Measurement;
import be.kdg.sensorservice.repositories.MeasurementRepository;
import be.kdg.sensorservice.repositories.specifications.MeasurementSpecifications;
import be.kdg.sensorservice.services.MeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MeasurementService implementation
 */
@Service
@Transactional
public class MeasurementServiceImpl implements MeasurementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementServiceImpl.class);
    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementServiceImpl(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public List<Measurement> getMeasurements(Filter filter) {
        MeasurementSpecifications specifications = new MeasurementSpecifications(filter);
        return measurementRepository.findAll(specifications);
    }

    @Override
    public Measurement getMeasurementById(long id) {
        return measurementRepository
            .findById(id)
            .orElseThrow(() -> new InternalSensorServiceException(String.format("Measurement with ID %d not found", id)));
    }

    @Override
    public void saveMeasurement(Measurement measurement) {
        measurementRepository.save(measurement);
        LOGGER.info("Measurement saved: " + measurement.toString());
    }
}
