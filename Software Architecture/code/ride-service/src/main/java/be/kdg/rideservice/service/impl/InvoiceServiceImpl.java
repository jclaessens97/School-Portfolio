package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.controllers.sender.MessageSender;
import be.kdg.rideservice.domain.model.pricing.PriceInfoProxy;
import be.kdg.rideservice.domain.model.pricing.calculator.PricingCalculator;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.dto.messages.impl.InvoiceMessage;
import be.kdg.rideservice.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private final PriceInfoProxy priceInfoProxy;
    private final PricingCalculator pricingCalculator;
    private final MessageSender<InvoiceMessage> messageSender;

    @Autowired
    public InvoiceServiceImpl(PriceInfoProxy priceInfoProxy, PricingCalculator pricingCalculator, MessageSender<InvoiceMessage> messageSender) {
        this.priceInfoProxy = priceInfoProxy;
        this.pricingCalculator = pricingCalculator;
        this.messageSender = messageSender;
    }

    public double calculatePrice(int subscriptionType, int vehicleType, LocalDateTime startTime, LocalDateTime endTime) {
        final int freeMinutes = priceInfoProxy.getFreeMinutes(subscriptionType, vehicleType);
        final int centsPerMinute = priceInfoProxy.getCentsPerMinute(subscriptionType, vehicleType);
        final long duration = ChronoUnit.MINUTES.between(startTime, endTime);

        return pricingCalculator.calculatePrice(freeMinutes, centsPerMinute, duration);
    }

    public void sendInvoice(Ride ride) {
        final int subscriptionType = ride.getSubscription().getSubscriptionType().getSubscriptionTypeId();
        final int vehicleType = ride.getVehicle().getBikeLot().getBikeType().getBikeTypeId();
        final LocalDateTime startTime = ride.getStartTime();
        final LocalDateTime endTime = ride.getEndTime();

        final double price = calculatePrice(subscriptionType, vehicleType, startTime, endTime);
        InvoiceMessage message = new InvoiceMessage(
            ride.getSubscription().getUser().getUserId(),
            price,
            ride
        );
        messageSender.send(message);
        LOGGER.info("Invoice message sent.");
    }
}
