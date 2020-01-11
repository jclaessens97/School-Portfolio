package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("get_free_locks")
public class GetFreeLocksCommand extends BaseCommand {
    private final RestSender restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public GetFreeLocksCommand(ObjectMapper mapper, RestSender restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setStationId(mapper.convertValue(values[0], Short.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("get_free_locks");
        url = String.format("%s?stationId=%d", url, ctx.getStationId());

        ResponseEntity response = restSender.getRequest(url, Short[].class);
        Short[] freeLockIds = mapper.convertValue(response.getBody(), Short[].class);

        if (freeLockIds != null) {
            ctx.setFreeLockIds(Arrays.asList(freeLockIds));
        }

        return ctx;
    }
}
