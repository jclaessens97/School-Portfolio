package be.kdg.rideservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NearestVehicleDto {
    @Setter
    private short vehicleId;
    private double xCoord;
    private double yCoord;

    public void setPoint(Point point) {
        this.xCoord = point.getX();
        this.yCoord = point.getY();
    }
}
