package be.kdg.cluedobackend.integration;

import be.kdg.cluedobackend.dto.LobbyDetailsDto;
import be.kdg.cluedobackend.dto.LobbyDto;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.helpers.MockSecurityContext;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import be.kdg.cluedobackend.services.LobbyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LobbyApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CluedoRepository cluedoRepository;

    @Autowired
    private LobbyService lobbyService;

    private List<User> users;

    private List<Integer> cluedoIds;

    @Before
    public void SetUp() {
        users = new ArrayList<>();
        cluedoIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(new User(UUID.randomUUID(), "User " + i, List.of(Role.USER)));
        }

        userRepository.saveAll(users);
    }

    @After
    public void tearDown() {
        for (Integer cluedoId :
                cluedoIds) {
            cluedoRepository.deleteById(cluedoId);
        }

        userRepository.deleteAll(users);
    }

    @Test
    public void createLobby() throws Exception {
        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            post("/api/lobby/create")
            .contentType(MediaType.APPLICATION_JSON)
            .param("characterType", CharacterType.GREEN.toString())
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        int cluedoId = Integer.parseInt(response);
        Assert.assertTrue(cluedoId > 0);
        cluedoIds.add(cluedoId);

        Cluedo cluedo = lobbyService.getLobbyById(cluedoId);

        Assert.assertEquals(1, cluedo.getPlayers().size());
        Assert.assertEquals(cluedo.getPlayers().get(0).getUser(), users.get(0));
    }

    @Test
    public void listLobbies() throws Exception {
        int lobby1 = lobbyService.createLobby(users.get(1).getUserId(), CharacterType.BLUE);
        int lobby2 = lobbyService.createLobby(users.get(2).getUserId(), CharacterType.BLUE);
        int lobby3 = lobbyService.createLobby(users.get(3).getUserId(), CharacterType.BLUE);

        cluedoIds.add(lobby1);
        cluedoIds.add(lobby2);
        cluedoIds.add(lobby3);

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            get("/api/lobby/list")
            .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        LobbyDto[] lobbies = objectMapper.readValue(response, LobbyDto[].class);
        Assert.assertTrue(lobbies.length >= 3);
        Assert.assertTrue(!Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby1 ).findFirst().isEmpty());
        Assert.assertTrue(!Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby2 ).findFirst().isEmpty());
        Assert.assertTrue(!Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby3 ).findFirst().isEmpty());
    }

    @Test
    public void listLobbiesWhenGameStarts() throws Exception {
        int lobby1 = lobbyService.createLobby(users.get(1).getUserId(), CharacterType.BLUE);
        int lobby2 = lobbyService.createLobby(users.get(2).getUserId(), CharacterType.BLUE);
        int lobby3 = lobbyService.createLobby(users.get(3).getUserId(), CharacterType.BLUE);

        cluedoIds.add(lobby1);
        cluedoIds.add(lobby2);
        cluedoIds.add(lobby3);

        lobbyService.joinLobby(lobby1, users.get(2).getUserId(), CharacterType.GREEN);
        lobbyService.joinLobby(lobby1, users.get(3).getUserId(), CharacterType.PURPLE);

        lobbyService.startGame(lobby1, users.get(1).getUserId());

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
                get("/api/lobby/list")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        LobbyDto[] lobbies = objectMapper.readValue(response, LobbyDto[].class);
        Assert.assertTrue(lobbies.length >= 2);
        Assert.assertTrue(Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby1 ).findFirst().isEmpty());
        Assert.assertTrue(!Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby2 ).findFirst().isEmpty());
        Assert.assertTrue(!Arrays.stream(lobbies).filter(l -> l.getCluedoId() == lobby3 ).findFirst().isEmpty());
    }

    @Test
    public void joinLobby() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(1).getUserId(), CharacterType.BLUE);

        cluedoIds.add(lobbyId);

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            post("/api/lobby/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("cluedoId", String.valueOf(lobbyId))
                    .param("characterType", CharacterType.GREEN.toString())

        )
        .andExpect(status().isCreated())
        .andReturn();

        Cluedo lobby = lobbyService.getLobbyById(lobbyId);

        Assert.assertEquals(2, lobby.getPlayers().size());
    }

    @Test
    public void joinLobbyCharacterAlreadyTaken() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(1).getUserId(), CharacterType.BLUE);

        cluedoIds.add(lobbyId);

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
                post("/api/lobby/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cluedoId", String.valueOf(lobbyId))
                        .param("characterType", CharacterType.BLUE.toString())
        )
                .andExpect(status().isBadRequest())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        JSONObject json = new JSONObject(response);

        String exceptionTypeStr = json.getString("message");
        CluedoExceptionType exceptionType = CluedoExceptionType.valueOf(exceptionTypeStr);

        Assert.assertEquals(CluedoExceptionType.CHARACTERTYPE_TAKEN, exceptionType);
    }

    @Test
    public void getLobbyDetails() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(1).getUserId(), CharacterType.BLUE);
        cluedoIds.add(lobbyId);

        MockSecurityContext.mockNormalUser(users.get(1).getUserId());

        MvcResult result = mockMvc.perform(
            get("/api/lobby/details")
            .contentType(MediaType.APPLICATION_JSON)
            .param("cluedoId", String.valueOf(lobbyId))
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        LobbyDetailsDto lobbyDetailsDto = objectMapper.readValue(response, LobbyDetailsDto.class);
        Assert.assertEquals(lobbyId, lobbyDetailsDto.getCluedoId());
    }

    @Test
    public void leaveLobby() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(0).getUserId(), CharacterType.BLUE);
        cluedoIds.add(lobbyId);

        lobbyService.joinLobby(lobbyId, users.get(1).getUserId(), CharacterType.GREEN);

        Cluedo lobby = lobbyService.getLobbyById(lobbyId);

        int playerId = lobbyService.getPlayerInLobby(lobby, users.get(0).getUserId()).getPlayerId();

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            delete("/api/lobby/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cluedoId", String.valueOf(lobbyId))
                .param("playerId", String.valueOf(playerId))
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        lobby = lobbyService.getLobbyById(lobbyId);

        Assert.assertEquals(1, lobby.getPlayers().size());
    }

    @Test(expected = CluedoException.class)
    public void leaveLobbyLastPlayer() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(0).getUserId(), CharacterType.BLUE);
        // cluedoIds.add(lobbyId);

        Cluedo lobby = lobbyService.getLobbyById(lobbyId);

        int playerId = lobbyService.getPlayerInLobby(lobby, users.get(0).getUserId()).getPlayerId();

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
                delete("/api/lobby/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("cluedoId", String.valueOf(lobbyId))
                        .param("playerId", String.valueOf(playerId))
        )
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        lobby = lobbyService.getLobbyById(lobbyId);
    }

    @Test
    public void kickLobby() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(0).getUserId(), CharacterType.BLUE);
        cluedoIds.add(lobbyId);

        lobbyService.joinLobby(lobbyId, users.get(1).getUserId(), CharacterType.GREEN);

        Cluedo lobby = lobbyService.getLobbyById(lobbyId);

        int playerToKicId = lobbyService.getPlayerInLobby(lobby, users.get(1).getUserId()).getPlayerId();

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            delete("/api/lobby/kick")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("cluedoId", String.valueOf(lobbyId))
                    .param("playerId", String.valueOf(playerToKicId))
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);

        lobby = lobbyService.getLobbyById(lobbyId);

        Assert.assertEquals(1, lobby.getPlayers().size());
    }

    @Test
    public void startLobby() throws Exception {
        int lobbyId = lobbyService.createLobby(users.get(0).getUserId(), CharacterType.BLUE);
        lobbyService.joinLobby(lobbyId, users.get(1).getUserId(), CharacterType.GREEN);
        lobbyService.joinLobby(lobbyId, users.get(2).getUserId(), CharacterType.PURPLE);
        lobbyService.joinLobby(lobbyId, users.get(3).getUserId(), CharacterType.RED);
        lobbyService.joinLobby(lobbyId, users.get(4).getUserId(), CharacterType.WHITE);
        lobbyService.joinLobby(lobbyId, users.get(5).getUserId(), CharacterType.YELLOW);
        cluedoIds.add(lobbyId);

        MockSecurityContext.mockNormalUser(users.get(0).getUserId());

        MvcResult result = mockMvc.perform(
            put("/api/lobby/start")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cluedoId", String.valueOf(lobbyId))
        )
        .andExpect(status().isOk())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
    }

    @Test
    public void changeGameSettings() throws Exception {

    }
 }
