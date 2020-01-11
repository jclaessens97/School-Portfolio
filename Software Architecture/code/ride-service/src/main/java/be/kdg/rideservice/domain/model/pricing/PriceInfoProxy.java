package be.kdg.rideservice.domain.model.pricing;

import be.kdg.rideservice.domain.exceptions.ExternalRideServiceException;
import be.kdg.sa.priceservice.Proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Wrapper for the 3rd party PriceInfoProxy
 */
@Component
public class PriceInfoProxy {
    private final Proxy priceInfoProxy;

    @Autowired
    public PriceInfoProxy(Proxy priceInfoProxy) {
        this.priceInfoProxy = priceInfoProxy;
    }

    @Cacheable("priceInfoFreeMinutes")
    @Retryable(value = ExternalRideServiceException.class)
    public int getFreeMinutes(int subscriptionType, int vehicleType) {
        try {
            return priceInfoProxy.get(subscriptionType, vehicleType).getFreeMinutes();
        } catch (IOException ex) {
            throw new ExternalRideServiceException("Could not retrieve free minutes from pricing service.");
        }
    }

    @Cacheable("priceInfoCentsPerMinute")
    @Retryable(value = ExternalRideServiceException.class)
    public int getCentsPerMinute(int subscriptionType, int vehicleType) {
        try {
            return priceInfoProxy.get(subscriptionType, vehicleType).getCentsPerMinute();
        } catch (IOException ex) {
            throw new ExternalRideServiceException("Could not retrieve cents per minute from pricing service.");
        }
    }
}
