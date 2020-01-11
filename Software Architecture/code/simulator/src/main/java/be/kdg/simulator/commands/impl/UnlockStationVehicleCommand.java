package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.exceptions.InternalSimulatorException;
import be.kdg.simulator.dto.UnlockStationVehicleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("unlock_station_vehicle")
public class UnlockStationVehicleCommand extends BaseCommand {
    private final RestSender<Short> restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public UnlockStationVehicleCommand(ObjectMapper mapper, RestSender<Short> restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setUserId(mapper.convertValue(values[0], Integer.class));
        ctx.setStationId(mapper.convertValue(values[1], Short.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        final String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("unlock_free_vehicle");
        final UnlockStationVehicleDto requestDto = new UnlockStationVehicleDto(ctx.getUserId(), ctx.getStationId());

        ResponseEntity<Short> response = restSender.postRequest(url, requestDto, Short.class);
        Short lockId = response.getBody();
        if (lockId != null) {
            ctx.setLockId(lockId);
        } else {
            throw new InternalSimulatorException("Could not unlock a station vehicle");
        }

        return ctx;
    }
}
