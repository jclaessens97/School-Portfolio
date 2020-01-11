package be.kdg.simulator.dto.messages.impl;

import be.kdg.simulator.domain.model.sensor.SensorType;
import be.kdg.simulator.dto.messages.Message;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

/**
 * Model class that represents a SensorMessage that is being send to rabbitMQ
 */
@Getter
@RequiredArgsConstructor
public class SensorMessage implements Message {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timeStamp;
    private final double xCoord;
    private final double yCoord;
    private final SensorType sensorType;
    private final double value;
}
