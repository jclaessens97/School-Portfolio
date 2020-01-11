package be.kdg.simulator.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements some methods that can be used by all inherited commands
 */
@AllArgsConstructor
public abstract class BaseCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseCommand.class);
    protected final ObjectMapper mapper;

    @Override
    public void delay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            LOGGER.warn("Simulation is interrupted");
        }
    }
}
