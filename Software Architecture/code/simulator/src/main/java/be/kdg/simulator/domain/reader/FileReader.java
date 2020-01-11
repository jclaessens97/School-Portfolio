package be.kdg.simulator.domain.reader;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Used to read all lines in a file that's uploaded (multipartfile)
 */
public interface FileReader {
    List<String> readLines(MultipartFile file);
}
