package be.kdg.rideservice.service.impl;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.repositories.SubscriptionRepository;
import be.kdg.rideservice.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription getSubscriptionByUserId(int userId) {
        return subscriptionRepository
            .findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(userId)
            .orElseThrow(() -> new InternalRideServiceException(
                    String.format("No subscription found for user with userId %d", userId))
            );
    }
}
