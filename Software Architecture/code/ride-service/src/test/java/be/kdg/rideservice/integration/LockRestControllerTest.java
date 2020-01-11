package be.kdg.rideservice.integration;

import be.kdg.rideservice.domain.exceptions.InternalRideServiceException;
import be.kdg.rideservice.domain.model.station.Lock;
import be.kdg.rideservice.dto.LockFreeVehicleDto;
import be.kdg.rideservice.dto.LockStationVehicleDto;
import be.kdg.rideservice.dto.UnlockFreeVehicleDto;
import be.kdg.rideservice.dto.UnlockStationVehicleDto;
import be.kdg.rideservice.service.LockService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LockRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LockService lockService;

    @Test
    public void getFreeLocksSuccess() throws Exception {
        List<Short> lockIds = new ArrayList<>();
        for (short i = 1; i <= 5; i++) {
            lockIds.add(i);
        }

        Mockito.when(lockService.getFreeLockIdsByStationId(anyShort())).thenReturn(lockIds);

        this.mockMvc.perform(
            get("/api/free_locks")
            .accept(MediaType.APPLICATION_JSON)
            .param("stationId", "1")
        )
        .andExpect(status().isOk());
    }

    @Test
    public void getFreeLocksNoContent() throws Exception {
        List<Short> lockIds = new ArrayList<>();

        Mockito.when(lockService.getFreeLockIdsByStationId(anyShort())).thenReturn(lockIds);

        this.mockMvc.perform(
                get("/api/free_locks")
                .accept(MediaType.APPLICATION_JSON)
                .param("stationId", "1")
        )
        .andExpect(status().isNoContent());
    }

    @Test
    public void getFreeLocksInternalRideException() throws Exception {
        List<Short> lockIds = new ArrayList<>();

        Mockito.when(lockService.getFreeLockIdsByStationId(anyShort())).thenThrow(new InternalRideServiceException("Internal Exception"));

        this.mockMvc.perform(
                get("/api/free_locks")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("stationId", "1")
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    public void unlockStationVehicle() throws Exception {
        UnlockStationVehicleDto unlockStationVehicleDto = new UnlockStationVehicleDto();
        unlockStationVehicleDto.setUserId(1);
        unlockStationVehicleDto.setStationId((short) 1);

        Lock lock = new Lock();
        lock.setLockId((short) 1);

        Mockito.when(lockService.unlockStationVehicle(anyInt(), anyShort())).thenReturn(lock);

        this.mockMvc.perform(
            post("/api/unlock/station_vehicle")
            .content(objectMapper.writeValueAsString(unlockStationVehicleDto))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void unlockStationVehicleInternalRideException() throws Exception {
        UnlockStationVehicleDto unlockStationVehicleDto = new UnlockStationVehicleDto();
        unlockStationVehicleDto.setUserId(1);
        unlockStationVehicleDto.setStationId((short) 1);

        Mockito.doThrow(new InternalRideServiceException("Internal Exception")).when(lockService).unlockStationVehicle(anyInt(), anyShort());

        this.mockMvc.perform(
            post("/api/unlock/station_vehicle")
                .content(objectMapper.writeValueAsString(unlockStationVehicleDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    public void unlockFreeVehicle() throws Exception {
        UnlockFreeVehicleDto unlockFreeVehicleDto = new UnlockFreeVehicleDto();
        unlockFreeVehicleDto.setUserId(1);
        unlockFreeVehicleDto.setVehicleId((short) 1);

        this.mockMvc.perform(
                post("/api/unlock/free_vehicle")
                        .content(objectMapper.writeValueAsString(unlockFreeVehicleDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void unlockFreeVehicleInternalRideException() throws Exception {
        UnlockFreeVehicleDto unlockFreeVehicleDto = new UnlockFreeVehicleDto();
        unlockFreeVehicleDto.setUserId(1);
        unlockFreeVehicleDto.setVehicleId((short) 1);

        Mockito.doThrow(new InternalRideServiceException("Internal Exception")).when(lockService).unlockFreeVehicle(anyInt(), anyShort());

        this.mockMvc.perform(
            post("/api/unlock/free_vehicle")
                .content(objectMapper.writeValueAsString(unlockFreeVehicleDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    public void lockStationVehicle() throws Exception {
        LockStationVehicleDto lockStationVehicleDto = new LockStationVehicleDto();
        lockStationVehicleDto.setUserId(1);
        lockStationVehicleDto.setLockId((short) 1);

        this.mockMvc.perform(
            post("/api/lock/station_vehicle")
                .content(objectMapper.writeValueAsString(lockStationVehicleDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void lockStationVehicleInternalRideException() throws Exception {
        LockStationVehicleDto lockStationVehicleDto = new LockStationVehicleDto();
        lockStationVehicleDto.setUserId(1);
        lockStationVehicleDto.setLockId((short) 1);

        Mockito.doThrow(new InternalRideServiceException("Internal Exception")).when(lockService).lockStationVehicle(anyInt(), anyShort());

        this.mockMvc.perform(
                post("/api/lock/station_vehicle")
                        .content(objectMapper.writeValueAsString(lockStationVehicleDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void lockFreeVehicle() throws Exception {
        LockFreeVehicleDto lockFreeVehicleDto = new LockFreeVehicleDto();
        lockFreeVehicleDto.setUserId(1);
        lockFreeVehicleDto.setVehicleId((short) 1);

        this.mockMvc.perform(
            post("/api/lock/free_vehicle")
                .content(objectMapper.writeValueAsString(lockFreeVehicleDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk());
    }

    @Test
    public void lockFreeVehicleInternalRideException() throws Exception {
        LockFreeVehicleDto lockFreeVehicleDto = new LockFreeVehicleDto();
        lockFreeVehicleDto.setUserId(1);
        lockFreeVehicleDto.setVehicleId((short) 1);

        Mockito.doThrow(new InternalRideServiceException("Internal Exception")).when(lockService).lockFreeVehicle(anyInt(), anyShort());

        this.mockMvc.perform(
            post("/api/lock/free_vehicle")
                .content(objectMapper.writeValueAsString(lockFreeVehicleDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest());
    }
}