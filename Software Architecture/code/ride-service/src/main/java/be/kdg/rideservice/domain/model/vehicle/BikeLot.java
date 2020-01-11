package be.kdg.rideservice.domain.model.vehicle;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Bikelots")
@Getter
@Setter
public class BikeLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short bikeLotId;

    @ManyToOne
    @JoinColumn(name = "BikeTypeId")
    private BikeType bikeType;
}
