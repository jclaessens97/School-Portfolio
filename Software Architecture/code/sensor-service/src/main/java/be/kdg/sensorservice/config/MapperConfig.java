package be.kdg.sensorservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to setup the mappers
 */
@Configuration
public class MapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Needed to deserialize timestamps
        return objectMapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
