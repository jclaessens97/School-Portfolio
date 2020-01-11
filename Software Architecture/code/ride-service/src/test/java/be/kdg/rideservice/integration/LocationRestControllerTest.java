package be.kdg.rideservice.integration;

import be.kdg.rideservice.domain.model.vehicle.BikeType;
import be.kdg.rideservice.domain.model.vehicle.Vehicle;
import be.kdg.rideservice.dto.FindNearestVehicleDto;
import be.kdg.rideservice.repositories.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyByte;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeometryFactory gf;

    @MockBean
    private VehicleRepository vehicleRepository;

    @Test
    public void getNearestFreeVehicle() throws Exception {
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleId((short) 1);
        vehicle.setPoint(gf.createPoint(new Coordinate(51, 23)));

        BikeType bikeType = new BikeType();
        bikeType.setBikeTypeId((byte) 1);

        FindNearestVehicleDto findNearestVehicleDto = new FindNearestVehicleDto();
        findNearestVehicleDto.setXCoord(51);
        findNearestVehicleDto.setYCoord(23);
        findNearestVehicleDto.setBikeType(bikeType);

        Mockito.when(vehicleRepository.findNearestVehicle(any(), anyByte())).thenReturn(Optional.of(vehicle));

        this.mockMvc.perform(
            post("/api/find_nearest_free_vehicle")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(objectMapper.writeValueAsString(findNearestVehicleDto))
        )
        .andExpect(status().isOk());
    }
}