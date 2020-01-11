package be.kdg.rideservice.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Needed to deserialize timestamps
        objectMapper.registerModule(new JtsModule()); // Needed to deserialize points
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS); // Needed to deserialize points
        return objectMapper;
    }
}
