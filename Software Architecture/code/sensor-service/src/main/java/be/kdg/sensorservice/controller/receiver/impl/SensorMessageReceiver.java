package be.kdg.sensorservice.controller.receiver.impl;

import be.kdg.sensorservice.controller.receiver.Receiver;
import be.kdg.sensorservice.domain.model.Measurement;
import be.kdg.sensorservice.dto.MeasurementDto;
import be.kdg.sensorservice.services.MeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Reads incoming sensormessages from the sensorqueue and maps them to the correct model class
 */
@Component
public class SensorMessageReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorMessageReceiver.class);
    private final MeasurementService measurementService;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public SensorMessageReceiver(MeasurementService measurementService, ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.measurementService = measurementService;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    @RabbitListener(queues = "sensorQueue")
    public void receive(Message msg) {
        try {
            MeasurementDto measurementDto = objectMapper.readValue(msg.getBody(), MeasurementDto.class);
            Measurement measurement = modelMapper.map(measurementDto, Measurement.class);
            measurementService.saveMeasurement(measurement);
        } catch (IOException ex) {
            LOGGER.error("Failed to deserialize message: " + msg);
        }
    }
}
