package be.kdg.rideservice.integration;

import be.kdg.rideservice.domain.exceptions.VehicleNotFoundException;
import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.domain.model.vehicle.BikeLot;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.VehicleDto;
import be.kdg.rideservice.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class VehicleRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeometryFactory gf;

    @MockBean
    private VehicleRepository vehicleRepository;

    private Vehicle vehicle;
    private VehicleDto vehicleDto;

    @Before
    public void setUp() throws Exception {
        vehicle = new Vehicle();
        vehicle.setVehicleId(null);

        BikeLot bikeLot = new BikeLot();
        bikeLot.setBikeLotId((short) 1);

        Lock lock = new Lock();
        lock.setLockId((short) 1);

        vehicleDto = new VehicleDto();
//        vehicleDto.setVehicleId(null);
        vehicleDto.setSerialNumber("abc");
//        vehicleDto.setBikeLot(bikeLot);
//        vehicleDto.setLock(lock);
        vehicleDto.setPoint(gf.createPoint(new Coordinate(51, 23)));
    }

    @Test
    public void getAllVehiclesSuccess() throws Exception {
        List<Vehicle> returnList = new ArrayList<>();
        returnList.add(vehicle);

        Mockito.when(vehicleRepository.findAll()).thenReturn(returnList);

        this.mockMvc.perform(
            get("/api/vehicles")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void getAllVehiclesNoContent() throws Exception {
        Mockito.when(vehicleRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(
                get("/api/vehicles")
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNoContent());
    }

    @Test
    public void getVehicleSuccess() throws Exception {
        Mockito.when(vehicleRepository.findById((short) 1)).thenReturn(Optional.of(vehicle));

        this.mockMvc.perform(
            get("/api/vehicles/1")
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void getVehicleNotFound() throws Exception {
        Mockito.when(vehicleRepository.findById(any())).thenThrow(new VehicleNotFoundException("Vehicle not found."));

        this.mockMvc.perform(
            get("/api/vehicles/1")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound());
    }

    @Test
    public void postVehicleSuccess() throws Exception {
        Mockito.when(vehicleRepository.save(any())).thenReturn(vehicleDto);

        this.mockMvc.perform(
            post("/api/vehicles")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(vehicleDto))
        )
        .andExpect(status().isCreated());
    }

    @Test
    public void putVehicleSuccess() throws Exception {
        Mockito.when(vehicleRepository.save(any())).thenReturn(vehicleDto);

        this.mockMvc.perform(
            put("/api/vehicles")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(vehicleDto))
        )
        .andExpect(status().isOk());
    }

    @Test
    public void deleteVehicleSuccess() throws Exception {
        Mockito.when(vehicleRepository.findById((short) 1)).thenReturn(Optional.of(vehicle));

        this.mockMvc.perform(
            delete("/api/vehicles/1")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void deleteVehicleNotfound() throws Exception {
        this.mockMvc.perform(
            delete("/api/vehicles/1")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound());
    }
}
