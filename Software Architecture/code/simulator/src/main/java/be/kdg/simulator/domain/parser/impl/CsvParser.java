package be.kdg.simulator.domain.parser.impl;

import be.kdg.simulator.domain.parser.Parser;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Parser implementation for CSV file which are comma-delimited
 */
@Component
public class CsvParser implements Parser {
    private Queue<String> linesToParse;

    @Override
    public void initialize(List<String> lines) {
        this.linesToParse = new LinkedList<>(lines);
    }

    @Override
    public boolean hasNextElement() {
        return linesToParse.size() > 0;
    }

    @Override
    public List<String> parseNextElement() {
        final String line = linesToParse.remove();
        return Arrays.asList(line.split(","));
    }
}
