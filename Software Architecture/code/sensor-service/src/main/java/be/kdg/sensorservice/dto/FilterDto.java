package be.kdg.sensorservice.dto;

import be.kdg.sensorservice.domain.model.SensorType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
public class FilterDto {
    private SensorType type;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Double xCoord;
    private Double yCoord;
    private Double variance;
}
