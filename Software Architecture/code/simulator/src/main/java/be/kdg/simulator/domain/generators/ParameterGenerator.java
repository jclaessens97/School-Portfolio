package be.kdg.simulator.domain.generators;

import be.kdg.simulator.commands.SimulatorCommandContext;

/**
 * Interface that defines the contract for parameter generators
 */
public interface ParameterGenerator {
    void initialize();
    void setContext(SimulatorCommandContext ctx);
    boolean hasNext();
    SimulatorCommandContext generateNext();
}
