package be.kdg.rideservice.controllers.api;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.dto.LockFreeVehicleDto;
import be.kdg.rideservice.dto.LockStationVehicleDto;
import be.kdg.rideservice.dto.UnlockFreeVehicleDto;
import be.kdg.rideservice.dto.UnlockStationVehicleDto;
import be.kdg.rideservice.service.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class LockRestController {
    private final LockService lockService;

    @Autowired
    public LockRestController(LockService lockService) {
        this.lockService = lockService;
    }

    @GetMapping(value = "/free_locks")
    public ResponseEntity getFreeLocks(@RequestParam short stationId) {
        List<Short> lockIds = lockService.getFreeLockIdsByStationId(stationId);
        if (lockIds.size() > 0) {
            return ResponseEntity.ok(lockIds);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/unlock/station_vehicle")
    public ResponseEntity<Short> unlockStationVehicle(@RequestBody UnlockStationVehicleDto unlockStationVehicleDto) {
        Lock lock = lockService.unlockStationVehicle(unlockStationVehicleDto.getUserId(), unlockStationVehicleDto.getStationId());
        return ResponseEntity.ok(lock.getLockId());
    }

    @PostMapping(value = "/unlock/free_vehicle")
    public ResponseEntity<Boolean> unlockFreeVehicle(@RequestBody UnlockFreeVehicleDto unlockFreeVehicleDto) {
        lockService.unlockFreeVehicle(unlockFreeVehicleDto.getUserId(), unlockFreeVehicleDto.getVehicleId());
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/lock/station_vehicle")
    public ResponseEntity lockStationVehicle(@RequestBody LockStationVehicleDto lockStationVehicleDto) {
        lockService.lockStationVehicle(lockStationVehicleDto.getUserId(), lockStationVehicleDto.getLockId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/lock/free_vehicle")
    public ResponseEntity<Boolean> lockFreeVehicle(@RequestBody LockFreeVehicleDto lockFreeVehicleDto) {
        lockService.lockFreeVehicle(lockFreeVehicleDto.getUserId(), lockFreeVehicleDto.getVehicleId());
        return ResponseEntity.ok(true);
    }
}
