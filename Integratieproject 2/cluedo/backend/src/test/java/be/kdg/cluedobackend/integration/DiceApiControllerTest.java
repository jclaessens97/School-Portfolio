package be.kdg.cluedobackend.integration;

import be.kdg.cluedobackend.helpers.MockSecurityContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class DiceApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void rollDiceTest() throws Exception {
        MockSecurityContext.mockNormalUser(UUID.randomUUID());

        mockMvc.perform(get("/api/dice/one")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"));
    }

    @Test
    public void rollTwoDiceTest() throws Exception {
        MockSecurityContext.mockNormalUser(UUID.randomUUID());

        mockMvc.perform(get("/api/dice/two")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.request().asyncNotStarted());
    }
}
