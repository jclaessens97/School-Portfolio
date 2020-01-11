package be.kdg.rideservice.domain.model.ride;

import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.subscription.Subscription;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Rides")
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rideId;

    @Column(columnDefinition = "Geometry")
    private Point startPoint;

    @Column(columnDefinition = "Geometry")
    private Point endPoint;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(name = "VehicleId")
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "SubscriptionId")
    private Subscription subscription;

    @ManyToOne
    @JoinColumn(name = "StartLockId")
    private Lock startLock;

    @ManyToOne
    @JoinColumn(name = "EndLockId")
    private Lock endLock;
}
