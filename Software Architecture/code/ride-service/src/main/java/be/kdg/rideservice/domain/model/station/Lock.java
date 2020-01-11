package be.kdg.rideservice.domain.model.station;

import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Locks")
@Getter
@Setter
public class Lock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short lockId;

    private Byte stationLockNr;

    @ManyToOne
    @JoinColumn(name = "StationId")
    private Station station;

    @OneToOne
    @JoinColumn(name = "VehicleId")
    private Vehicle vehicle;
}
