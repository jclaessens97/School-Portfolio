package be.kdg.simulator.commands.impl;

import be.kdg.simulator.commands.BaseCommand;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.controllers.sender.MessageSender;
import be.kdg.simulator.dto.messages.impl.SensorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SensorCommand extends BaseCommand {
    private final MessageSender<SensorMessage> messageSender;

    @Autowired
    public SensorCommand(ObjectMapper mapper, MessageSender<SensorMessage> messageSender) {
        super(mapper);
        this.messageSender = messageSender;
    }

    @Override
    public SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values) {
        throw new UnsupportedOperationException("Sensor command doesn't need to setup context");
    }

    @Override
    public SimulatorCommandContext execute(SimulatorCommandContext ctx) {
        messageSender.send(
            new SensorMessage(
                ctx.getTimeStamp(),
                ctx.getXCoord(),
                ctx.getYCoord(),
                ctx.getSensorType(),
                ctx.getSensorValue()
            )
        );

        return ctx;
    }
}

