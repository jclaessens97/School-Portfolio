package be.kdg.rideservice.domain.model.subscription;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotNull
    @Length(max = 100)
    private String name;

    @NotNull
    @Length(max = 100)
    private String email;

    @NotNull
    @Length(max = 100)
    private String street;

    @NotNull
    @Length(max = 10)
    private String number;

    @NotNull
    @Length(max = 10)
    private String zipcode;

    @NotNull
    @Length(max = 100)
    private String city;

    @NotNull
    @Length(max = 3)
    private String countryCode;
}
