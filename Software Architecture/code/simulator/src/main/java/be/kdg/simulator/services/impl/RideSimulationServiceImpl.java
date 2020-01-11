package be.kdg.simulator.services.impl;

import be.kdg.simulator.commands.Command;
import be.kdg.simulator.commands.SimulatorCommandContext;
import be.kdg.simulator.domain.exceptions.ExternalSimulatorException;
import be.kdg.simulator.domain.exceptions.InternalSimulatorException;
import be.kdg.simulator.domain.parser.Parser;
import be.kdg.simulator.domain.reader.FileReader;
import be.kdg.simulator.services.RideSimulationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class RideSimulationServiceImpl implements RideSimulationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RideSimulationService.class);
    private final FileReader fileReader;
    private final Parser parser;
    private final Map<String, Command> commands;
    private final ObjectMapper mapper;

    @Autowired
    public RideSimulationServiceImpl(FileReader fileReader, Parser parser, Map<String, Command> commands, ObjectMapper mapper) {
        this.fileReader = fileReader;
        this.parser = parser;
        this.commands = commands;
        this.mapper = mapper;
    }

    @Override
    @Async
    public void startSimulationAsync(MultipartFile file) {
        LOGGER.info("Running ride simulation");
        final List<String> lines = fileReader.readLines(file);
        SimulatorCommandContext ctx = new SimulatorCommandContext();
        parser.initialize(lines);

        while (parser.hasNextElement()) {
            try {
                List<String> values = parser.parseNextElement();

                if (values.size() != 3) {
                    throw new ExternalSimulatorException("Element does not have the required values to execute.");
                }

                // Retrieves commandname
                String commandName = values.get(0);
                Command cmd = commands.get(commandName);
                if (cmd == null) {
                    throw new InternalSimulatorException("No command found with commandname: " + commandName);
                }

                // Retrieves parameters needed to execute command
                ctx = cmd.createContext(ctx, values.get(1).split(";"));

                // Retrieves the delay performed between commands.
                long delayInMs = mapper.convertValue(values.get(2), Long.class);

                cmd.delay(delayInMs);
                ctx = cmd.execute(ctx);
                LOGGER.info(String.format("Ride simulation command %s executed", commandName));
            } catch (InternalSimulatorException | ExternalSimulatorException ex) {
                LOGGER.error("Ride simulator stopped working...");
                LOGGER.error(ex.getMessage());
                return;
            } catch (IllegalArgumentException ex) {
                LOGGER.error("Ride simulator stopped working...");
                LOGGER.error("Value is of wrong type");
                return;
            }
        }

        LOGGER.info("Ride simulation succesfully finished executing");
    }
}
