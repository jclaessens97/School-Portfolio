package be.kdg.rideservice.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SubscriptionTypes")
@Getter
@Setter
public class SubscriptionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte subscriptionTypeId;

    @NotNull
    @Length(max = 50)
    private String description;
}
