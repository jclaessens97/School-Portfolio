package be.kdg.simulator.config.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration class to enable async methods in the whole application.
 * ProxyTargetClass = true, to use CGLib-based proxies.
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class SpringAsyncConfig {
}
