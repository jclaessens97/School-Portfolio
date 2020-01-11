package be.kdg.sensorservice.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Model class that stores the filter values
 */
@Getter
@Setter
public class Filter {
    private SensorType type;
    private Date date;
    private Double xCoord;
    private Double yCoord;

    @Getter(AccessLevel.NONE)
    private Double variance;

    public Double getVariance() {
        if (variance == null) {
            return 0.0;
        }

        return variance;
    }
}
