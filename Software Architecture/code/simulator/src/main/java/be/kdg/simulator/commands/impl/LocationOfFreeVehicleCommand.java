package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.controllers.sender.MessageSender;
import be.kdg.simulator.dto.messages.impl.LocationMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component("location_of_free_vehicle")
public class LocationOfFreeVehicleCommand extends BaseCommand {
    private final MessageSender<LocationMessage> messageSender;
    private final DateTimeFormatter formatter;

    @Autowired
    public LocationOfFreeVehicleCommand(MessageSender<LocationMessage> messageSender, DateTimeFormatter formatter, ObjectMapper mapper) {
        super(mapper);
        this.messageSender = messageSender;
        this.formatter = formatter;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        ctx.setTimeStamp(LocalDateTime.parse(values[0]));
        ctx.setVehicleId(mapper.convertValue(values[1], Short.class));
        ctx.setXCoord(mapper.convertValue(values[2], Double.class));
        ctx.setYCoord(mapper.convertValue(values[3], Double.class));
        return ctx;
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        messageSender.send(
            new LocationMessage(
                ctx.getTimeStamp(),
                ctx.getVehicleId(),
                ctx.getXCoord(),
                ctx.getYCoord()
            )
        );

        return ctx;
    }
}
