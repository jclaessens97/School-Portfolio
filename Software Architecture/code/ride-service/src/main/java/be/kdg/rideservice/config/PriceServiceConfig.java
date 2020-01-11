package be.kdg.rideservice.config;

import be.kdg.sa.priceservice.Proxy;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceServiceConfig {
    @Bean
    public Proxy proxy() {
        return new Proxy();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("priceInfoFreeMinutes", "priceInfoCentsPerMinute");
    }
}
