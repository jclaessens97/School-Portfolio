package be.kdg.rideservice.domain.model.pricing.calculator;

/**
 * Calculates price for a ride based on free minutes, cents per minute and the duration of a ride in minutes.
 */
public interface PricingCalculator {
    public double calculatePrice(int freeMinutes, int centsPerMinute, long duration);
}
