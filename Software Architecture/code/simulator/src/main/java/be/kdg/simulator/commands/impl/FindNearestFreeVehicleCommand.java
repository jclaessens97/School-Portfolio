package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.config.rideservice.RideServiceProperties;
import be.kdg.simulator.controllers.sender.RestSender;
import be.kdg.simulator.domain.model.ride.BikeType;
import be.kdg.simulator.dto.FindNearestVehicleDto;
import be.kdg.simulator.dto.NearestVehicleDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("find_nearest_free_vehicle")
public class FindNearestFreeVehicleCommand extends BaseCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(FindNearestFreeVehicleCommand.class);
    private final RestSender<NearestVehicleDto> restSender;
    private final RideServiceProperties rideServiceProperties;

    @Autowired
    public FindNearestFreeVehicleCommand(ObjectMapper mapper, RestSender<NearestVehicleDto> restSender, RideServiceProperties rideServiceProperties) {
        super(mapper);
        this.restSender = restSender;
        this.rideServiceProperties = rideServiceProperties;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setXCoord(mapper.convertValue(values[0], Double.class));
        ctx.setYCoord(mapper.convertValue(values[1], Double.class));
        ctx.setBikeType(mapper.convertValue(values[2], BikeType.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        final double xCoord = ctx.getXCoord();
        final double yCoord = ctx.getYCoord();
        final BikeType bikeType = ctx.getBikeType();

        final String url = rideServiceProperties.getBaseUri() + rideServiceProperties.getRoutes().get("find_nearest_free_vehicle");
        final FindNearestVehicleDto requestDto = new FindNearestVehicleDto(xCoord, yCoord, bikeType);

        ResponseEntity<NearestVehicleDto> response = restSender.postRequest(url, requestDto, NearestVehicleDto.class);
        NearestVehicleDto nearestVehicle = response.getBody();
        if (nearestVehicle != null) {
            ctx.setVehicleId(nearestVehicle.getVehicleId());
            ctx.setXCoord(nearestVehicle.getXCoord());
            ctx.setXCoord(nearestVehicle.getYCoord());
        }

        return ctx;
    }
}
