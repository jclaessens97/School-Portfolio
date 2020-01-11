package be.kdg.rideservice.domain.model.station;

import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "Stations")
@Getter
@Setter
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short stationId;

    @NotNull
    @Length(max = 20)
    private String objectId;

    @NotNull
    @Length(max = 20)
    private String stationNr;

    @NotNull
    @Length(max = 20)
    private String type;

    @NotNull
    @Length(max = 100)
    private String street;

    @NotNull
    @Length(max = 20)
    private String number;

    @NotNull
    @Length(max = 20)
    private String zipCode;

    @NotNull
    @Length(max = 100)
    private String district;

    @Column(columnDefinition = "Geometry")
    private Point GPSCoord;

    @Length(max = 100)
    private String additionalInfo;
}
