package be.kdg.rideservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Class that represents an incoming location message.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class LocationDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timeStamp;
    private short vehicleId;
    private double xCoord;
    private double yCoord;

    public LocationDto(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }
}
