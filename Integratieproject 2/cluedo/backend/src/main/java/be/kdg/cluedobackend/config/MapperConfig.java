package be.kdg.cluedobackend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper m = new ObjectMapper();
        m.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        m.registerModule(new JavaTimeModule());
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return m;
    }
}
