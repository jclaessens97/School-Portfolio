package be.kdg.rideservice.controllers.api;

import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.VehicleDto;
import be.kdg.rideservice.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class VehicleRestController {
    private final VehicleService vehicleService;
    private final ObjectMapper objectMapper;

    @Autowired
    public VehicleRestController(VehicleService vehicleService, ObjectMapper objectMapper) {
        this.vehicleService = vehicleService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        if (vehicles.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<VehicleDto> vehicleDtos = vehicles.stream().map(v -> objectMapper.convertValue(v, VehicleDto.class)).collect(Collectors.toList());
        return ResponseEntity.ok(vehicleDtos);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleDto> getVehicle(@PathVariable short id){
        Vehicle vehicle = vehicleService.getVehicleById(id);
        VehicleDto vehicleDto = objectMapper.convertValue(vehicle, VehicleDto.class);
        return ResponseEntity.ok(vehicleDto);
    }

    @PostMapping("/vehicles")
    public ResponseEntity<VehicleDto> addVehicle(@RequestBody VehicleDto vehicleDto) {
        Vehicle vehicle = objectMapper.convertValue(vehicleDto, Vehicle.class);
        Vehicle createdVehicle = vehicleService.addVehicle(vehicle);
        VehicleDto createdVehicleDto = objectMapper.convertValue(createdVehicle, VehicleDto.class);
        return new ResponseEntity<VehicleDto>(createdVehicleDto, HttpStatus.CREATED);
    }

    @PutMapping("/vehicles/{id}")
    public ResponseEntity updateVehicle(@RequestBody VehicleDto vehicleDto) {
        Vehicle vehicle = objectMapper.convertValue(vehicleDto, Vehicle.class);
        vehicleService.updateVehicle(vehicle);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity deleteVehicle(@PathVariable("id") short id){
        vehicleService.deleteVehicleById(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
