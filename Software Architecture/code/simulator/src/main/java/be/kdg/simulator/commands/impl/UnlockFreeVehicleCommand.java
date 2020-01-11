package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.exceptions.InternalSimulatorException;
import be.kdg.simulator.dto.UnlockFreeVehicleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("unlock_free_vehicle")
public class UnlockFreeVehicleCommand extends BaseCommand {
    private final RestSender<Boolean> restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public UnlockFreeVehicleCommand(ObjectMapper mapper, RestSender<Boolean> restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setUserId(mapper.convertValue(values[0], Integer.class));
        ctx.setVehicleId(mapper.convertValue(values[1], Short.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        final int userId = ctx.getUserId();
        final short vehicleId = ctx.getVehicleId();

        final String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("unlock_free_vehicle");
        final UnlockFreeVehicleDto requestDto = new UnlockFreeVehicleDto(ctx.getUserId(), ctx.getVehicleId());

        ResponseEntity<Boolean> response = restSender.postRequest(url, requestDto, Boolean.class);
        Boolean unlocked = response.getBody();
        if (unlocked != null) {
            if (!unlocked) {
                throw new InternalSimulatorException("Vehicle could not be unlocked");
            }
        }

        return ctx;
    }
}
