package be.kdg.rideservice.domain.model.pricing.calculator.impl;

import be.kdg.rideservice.domain.model.pricing.calculator.PricingCalculator;
import org.springframework.stereotype.Component;

@Component
public class PricingCalculatorImpl implements PricingCalculator {
    @Override
    public double calculatePrice(int freeMinutes, int centsPerMinute, long duration) {
        return (duration - freeMinutes) * centsPerMinute / 100.0;
    }
}
