package be.kdg.rideservice.repositories;

import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.subscription.SubscriptionType;
import be.kdg.rideservice.domain.model.subscription.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Date;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SubscriptionRepositoryTest {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    @Transactional
    public void checkMapping() {
        SubscriptionType subscriptionType = new SubscriptionType();
        subscriptionType.setSubscriptionTypeId((byte) 1);

        User user = new User();
        user.setUserId(1);

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setValidFrom(new Date());
        subscription.setSubscriptionType(subscriptionType);

        Subscription savedSubscription = subscriptionRepository.save(subscription);
        Assert.assertNotNull(savedSubscription);
        Assert.assertNotNull(savedSubscription);
        Assert.assertEquals(subscription.getSubscriptionType(), savedSubscription.getSubscriptionType());
        Assert.assertEquals(subscription.getUser(), savedSubscription.getUser());
        subscriptionRepository.delete(savedSubscription);
    }
}
