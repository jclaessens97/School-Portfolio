package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.ride.Ride;

import java.time.LocalDateTime;

/**
 * Creates an invoice message to push on a queue.
 */
public interface InvoiceService {
    public double calculatePrice(int subscriptionType, int vehicleType, LocalDateTime startTime, LocalDateTime endTime);
    public void sendInvoice(Ride ride);
}
