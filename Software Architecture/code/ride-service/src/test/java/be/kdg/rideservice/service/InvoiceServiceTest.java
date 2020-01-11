package be.kdg.rideservice.service;

import be.kdg.rideservice.controllers.sender.MessageSender;
import be.kdg.rideservice.domain.model.pricing.PriceInfoProxy;
import be.kdg.rideservice.domain.model.ride.Ride;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.subscription.SubscriptionType;
import be.kdg.rideservice.domain.model.subscription.User;
import be.kdg.rideservice.domain.model.vehicle.BikeLot;
import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.messages.impl.InvoiceMessage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InvoiceServiceTest {
    @MockBean private PriceInfoProxy proxy;
    @MockBean private MessageSender<InvoiceMessage> messageSender;

    @Autowired
    private InvoiceService invoiceService;

    @Test
    public void testCalculatePrice() {
        Mockito.when(proxy.getFreeMinutes(anyInt(), anyInt())).thenReturn(60);
        Mockito.when(proxy.getCentsPerMinute(anyInt(), anyInt())).thenReturn(15);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusMinutes(1000);
        double price = invoiceService.calculatePrice(0, 0, startTime, endTime);
        Assert.assertEquals(141.0, price, 0.01);
    }

    @Test
    public void testSendInvoice() {
        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setSubscriptionTypeId((byte) 1);

        User user = new User();
        user.setUserId(1);

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionType);
        subscription.setUser(user);

        BikeType bikeType = new BikeType();
        bikeType.setBikeTypeId((byte) 1);

        BikeLot bikeLot = new BikeLot();
        bikeLot.setBikeType(bikeType);

        Vehicle vehicle = new Vehicle();
        vehicle.setBikeLot(bikeLot);

        Ride ride = new Ride();
        ride.setSubscription(subscription);
        ride.setVehicle(vehicle);
        ride.setStartTime(LocalDateTime.now());
        ride.setEndTime(LocalDateTime.now().plusHours(1));

        invoiceService.sendInvoice(ride);
    }
}
