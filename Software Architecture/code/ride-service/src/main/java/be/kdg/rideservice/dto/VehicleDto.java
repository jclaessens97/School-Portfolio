package be.kdg.rideservice.dto;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.vehicle.BikeLot;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDto {
    private Short vehicleId;
    private String serialNumber;
    private BikeLot bikeLot;
    private Lock lock;
    private Point point;
}
