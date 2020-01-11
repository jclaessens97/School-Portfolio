package be.kdg.rideservice.domain.model.vehicle;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

@Entity
@Table(name = "BikeTypes")
@Getter
@Setter
public class BikeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Byte bikeTypeId;

    @Length(max = 200)
    private String bikeTypeDescription;
}
