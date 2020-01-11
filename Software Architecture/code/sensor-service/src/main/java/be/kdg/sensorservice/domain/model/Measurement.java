package be.kdg.sensorservice.domain.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Db Model class that stores measurement values
 */
@Entity
@Getter
@Setter
public class Measurement implements Comparable<Measurement> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private LocalDateTime timeStamp;
    private double xCoord;
    private double yCoord;
    private SensorType sensorType;
    private double value;

    @Override
    public String toString() {
        return String.format("Timestamp: %s | Coordinates: %8f, %8f | Type: %11s | Value: %f", timeStamp, xCoord,yCoord, sensorType, value);
    }

    @Override
    public int compareTo(Measurement o) {
        return o.getTimeStamp().isAfter(this.timeStamp)? -1 : 1;
    }
}
