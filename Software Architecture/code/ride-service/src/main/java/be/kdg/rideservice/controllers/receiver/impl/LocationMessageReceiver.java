package be.kdg.rideservice.controllers.receiver.impl;

import be.kdg.rideservice.controllers.receiver.Receiver;
import be.kdg.rideservice.dto.LocationDto;
import be.kdg.rideservice.service.RideService;
import be.kdg.rideservice.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LocationMessageReceiver implements Receiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocationMessageReceiver.class);
    private final ObjectMapper objectMapper;
    private final GeometryFactory gf;
    private final VehicleService vehicleService;
    private final RideService rideService;

    @Autowired
    public LocationMessageReceiver(ObjectMapper objectMapper, GeometryFactory geometryFactory, VehicleService vehicleService, RideService rideService) {
        this.objectMapper = objectMapper;
        this.gf = geometryFactory;
        this.vehicleService = vehicleService;
        this.rideService = rideService;
    }

    @RabbitListener(queues = "locationQueue")
    public void receive(Message msg) {
        try {
            LocationDto locationDto = objectMapper.readValue(msg.getBody(), LocationDto.class);
            Point point = gf.createPoint(new Coordinate(locationDto.getXCoord(), locationDto.getYCoord()));
            vehicleService.saveLocation(point, locationDto.getVehicleId());
            rideService.saveLocation(locationDto);
        } catch (IOException ex) {
            LOGGER.error("Failed to deserialize message" + msg);
        }
    }
}
