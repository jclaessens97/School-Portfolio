package be.kdg.simulator.domain.parser;

import java.util.List;

/**
 * Used to parse elements from a file
 * An element could be a tag in XML or a line in a CSV file for example.
 */
public interface Parser {
    void initialize(List<String> lines);
    boolean hasNextElement();
    List<String> parseNextElement();
}
