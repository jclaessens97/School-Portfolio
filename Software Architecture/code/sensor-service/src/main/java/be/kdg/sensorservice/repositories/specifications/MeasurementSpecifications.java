package be.kdg.sensorservice.repositories.specifications;

import be.kdg.sensorservice.domain.model.Filter;
import be.kdg.sensorservice.domain.model.Measurement;
import be.kdg.sensorservice.domain.model.SensorType;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class that implements JpaSpecifications to filter the data on database level
 * Filters are implemented as predicates at the bottom
 */
public class MeasurementSpecifications implements Specification<Measurement> {
    private final Filter filter;

    public MeasurementSpecifications(Filter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Measurement> root, CriteriaQuery<?> q, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getType() != null) {
            predicates.add(hasType(filter.getType()).toPredicate(root, q, cb));
        }

        if (filter.getDate() != null) {
            predicates.add(hasDate(filter.getDate()).toPredicate(root, q, cb));
        }

        if (filter.getXCoord() != null) {
            predicates.add(hasXCoord(filter.getYCoord(), filter.getVariance()).toPredicate(root, q, cb));
        }

        if (filter.getYCoord() != null) {
            predicates.add(hasYCoord(filter.getYCoord(), filter.getVariance()).toPredicate(root, q, cb));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    //#region Filter Predicates
    private Specification<Measurement> hasType(SensorType sensorType) {
        return (measurement, cq, cb) -> cb.equal(measurement.get("sensorType"), sensorType);
    }

    private Specification<Measurement> hasDate(Date timeStamp) {
        LocalDate localDate = timeStamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDateTime start = LocalDateTime.of(localDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(localDate, LocalTime.MAX);
        return (measurement, cq, cb) ->  cb.between(measurement.get("timeStamp"), start, end);
    }

    private Specification<Measurement> hasXCoord(double xCoord, double variance) {
        final double min = xCoord - variance;
        final double max = xCoord + variance;
        return (measurement, cq, cb) -> cb.between(measurement.get("xCoord"), min, max);
    }

    private Specification<Measurement> hasYCoord(double yCoord, double variance) {
        final double min = yCoord - variance;
        final double max = yCoord + variance;
        return (measurement, cq, cb) -> cb.between(measurement.get("yCoord"), min, max);
    }
    //#endregion
}
