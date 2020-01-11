package be.kdg.rideservice.dto.messages.impl;

import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.dto.messages.Message;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InvoiceMessage implements Message {
    private final int userId;
    private final double price;
    private final Ride ride;
}
