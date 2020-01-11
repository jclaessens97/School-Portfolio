package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.exceptions.InternalSimulatorException;
import be.kdg.simulator.dto.LockFreeVehicleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("lock_free_vehicle")
public class LockFreeVehicleCommand extends BaseCommand {
    private final RestSender<Boolean> restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public LockFreeVehicleCommand(ObjectMapper mapper, RestSender<Boolean> restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setVehicleId(mapper.convertValue(values[0], Short.class));
        ctx.setUserId(mapper.convertValue(values[1], Integer.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        final String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("lock_free_vehicle");
        final LockFreeVehicleDto requestDto = new LockFreeVehicleDto(ctx.getUserId(), ctx.getVehicleId());

        ResponseEntity<Boolean> response = restSender.postRequest(url, requestDto, Boolean.class);
        Boolean locked = response.getBody();
        if (locked != null) {
            if (!locked) {
                throw new InternalSimulatorException("Vehicle could not be locked");
            }
        }

        return ctx;
    }
}
