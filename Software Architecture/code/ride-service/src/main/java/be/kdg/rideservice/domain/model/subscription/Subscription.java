package be.kdg.rideservice.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "Subscriptions")
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subscriptionId;

    @NotNull
    private Date validFrom;

    @ManyToOne
    @JoinColumn(name = "SubscriptionTypeId")
    private SubscriptionType subscriptionType;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;
}
