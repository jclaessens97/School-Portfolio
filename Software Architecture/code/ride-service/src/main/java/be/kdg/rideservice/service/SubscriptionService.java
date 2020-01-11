package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.model.subscription.Subscription;

public interface SubscriptionService {
    Subscription getSubscriptionByUserId(int userId);
}
