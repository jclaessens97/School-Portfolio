package be.kdg.rideservice.controllers.api;

import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.FindNearestVehicleDto;
import be.kdg.rideservice.dto.NearestVehicleDto;
import be.kdg.rideservice.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LocationRestController {
    private final VehicleService vehicleService;
    private final ObjectMapper objectMapper;

    @Autowired
    public LocationRestController(VehicleService vehicleService, ObjectMapper objectMapper) {
        this.vehicleService = vehicleService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/find_nearest_free_vehicle")
    public ResponseEntity<NearestVehicleDto> getNearestFreeVehicle(@RequestBody FindNearestVehicleDto findNearestVehicleDto) {
        Vehicle vehicle =  vehicleService.findNearestFreeVehicle(
            findNearestVehicleDto.getXCoord(),
            findNearestVehicleDto.getYCoord(),
            findNearestVehicleDto.getBikeType()
        );

        NearestVehicleDto nearestVehicleDto = objectMapper.convertValue(vehicle, NearestVehicleDto.class);
        return ResponseEntity.ok(nearestVehicleDto);
    }
}
