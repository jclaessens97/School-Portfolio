package be.kdg.rideservice.domain.model.vehicle;

import be.kdg.rideservice.domain.model.station.Lock;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Vehicles")
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short vehicleId;

    @NotNull
    @Length(max = 20)
    private String serialNumber;

    @ManyToOne
    @JoinColumn(name = "BikeLotId")
    @NotNull
    private BikeLot bikeLot;

    @OneToOne
    @JoinColumn(name = "LockId")
    private Lock lock;

    @Column(columnDefinition = "Geometry")
    private Point point;
}
