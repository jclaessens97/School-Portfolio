package be.kdg.simulator.commands;

/**
 * Represents a command that is executed in the simulations.
 * Most of the commands are required to create and parse the parameters needed for execution first.
 */
public interface Command {
    SimulatorCommandContext createContext(SimulatorCommandContext ctx, String[] values);
    SimulatorCommandContext execute(SimulatorCommandContext ctx);
    void delay(long ms);
}
