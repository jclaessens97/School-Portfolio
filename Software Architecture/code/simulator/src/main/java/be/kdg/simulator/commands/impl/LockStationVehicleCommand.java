package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.exceptions.InternalSimulatorException;
import be.kdg.simulator.dto.LockStationVehicleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("lock_station_vehicle")
public class LockStationVehicleCommand extends BaseCommand {
    private final RestSender<Object> restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public LockStationVehicleCommand(ObjectMapper mapper, RestSender<Object> restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setUserId(mapper.convertValue(values[0], Integer.class));
        ctx.setLockId(mapper.convertValue(values[1], Short.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        final String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("lock_station_vehicle");
        final LockStationVehicleDto requestDto = new LockStationVehicleDto(ctx.getUserId(), ctx.getLockId());

        ResponseEntity response = restSender.postRequest(url, requestDto, Object.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new InternalSimulatorException("vehicle could not be locked");
        }

        return ctx;
    }
}
