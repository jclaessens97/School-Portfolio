package be.kdg.simulator.domain.reader.impl;

import be.kdg.simulator.domain.exceptions.ExternalSimulatorException;
import be.kdg.simulator.domain.reader.FileReader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileReaderImpl implements FileReader {
    @Override
    public List<String> readLines(MultipartFile file) {
        List<String> lines = new ArrayList<>();

        try {
            byte[] bytes = file.getBytes();
            ByteArrayInputStream inputFileStream = new ByteArrayInputStream(bytes);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream));
            String line = "";

            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException ex) {
            throw new ExternalSimulatorException("Could not read the file", ex);
        }

        return lines;
    }
}

