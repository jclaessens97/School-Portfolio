package be.kdg.rideservice.service;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.repositories.SubscriptionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SubscriptionServiceTest {
    @MockBean private SubscriptionRepository subscriptionRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Test(expected = InternalRideServiceException.class)
    public void getSubscriptionByUserIdNotFound() {
        Mockito.when(subscriptionRepository.findFirstSubscriptionByUser_UserIdOrderByValidFromDesc(anyInt())).thenReturn(Optional.empty());
        subscriptionService.getSubscriptionByUserId(1);
    }
}
