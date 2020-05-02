package be.kdg.cluedobackend.integration;

import be.kdg.cluedobackend.dto.ApiError;
import be.kdg.cluedobackend.dto.notebook.UpdateNoteColumnDto;
import be.kdg.cluedobackend.dto.notebook.UpdateNoteLineDto;
import be.kdg.cluedobackend.helpers.MockSecurityContext;
import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.notebook.NotationSymbol;
import be.kdg.cluedobackend.model.notebook.NoteBook;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.NoteBookRepository;
import be.kdg.cluedobackend.services.NoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
public class NoteApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteBookRepository noteBookRepository;

    private Player player;
    private NoteBook savedNoteBook;

    @Before
    public void setUp() throws Exception {
        player = new Player();
        User user = new User();
        user.setUserId(UUID.randomUUID());
        player.setUser(user);
        NoteBook nb = noteService.initializeNoteBook(6);
        nb.setPlayer(player);
        this.savedNoteBook = noteService.createNotebook(nb);
    }

    @Test
    public void updateColumnInExistingNotebook() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        UpdateNoteColumnDto dto = new UpdateNoteColumnDto(
                0,
                CardType.WEAPON,
                0,
                0,
                NotationSymbol.CROSS
        );

        MvcResult result = mockMvc.perform(put("/api/notes/update/column")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isBadRequest())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        ApiError errorObj = objectMapper.readValue(response, ApiError.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorObj.getHttpStatus());
        Assert.assertEquals("NOTEBOOK_NOT_FOUND", errorObj.getMessage());
    }

    @Test
    public void checkAllColumnsBlankTest() throws Exception {
        savedNoteBook.getCharacters().forEach((line) -> {
            line.getColumns().forEach((symbol) -> {
                Assert.assertEquals(NotationSymbol.EMPTY, symbol);
            });
        });

        savedNoteBook.getWeapons().forEach((line) -> {
            line.getColumns().forEach((symbol) -> {
                Assert.assertEquals(NotationSymbol.EMPTY, symbol);
            });
        });

        savedNoteBook.getRooms().forEach((line) -> {
            line.getColumns().forEach((symbol) -> {
                Assert.assertEquals(NotationSymbol.EMPTY, symbol);
            });
        });
    }

    @Test
    @WithMockUser
    public void updateAllCharactersColumnLinesTest() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        for (int i = 0; i < savedNoteBook.getCharacters().size(); i++) {
            for (int j = 0; j < savedNoteBook.getNumberOfColumns(); j++) {
                UpdateNoteColumnDto dto = new UpdateNoteColumnDto(
                    savedNoteBook.getNoteBookId(),
                    CardType.CHARACTER,
                    i,
                    j,
                    NotationSymbol.CROSS
                );

                mockMvc.perform(put("/api/notes/update/column")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

                NoteBook retrievedNb = noteBookRepository.findById(savedNoteBook.getNoteBookId()).get();
                Assert.assertNotNull(retrievedNb);
                matchColumnSymbolOrFail(retrievedNb, NotationSymbol.CROSS, CardType.CHARACTER, i, j);
            }
        }
    }

    @Test
    public void updateAllWeaponsColumnLinesTest() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        for (int i = 0; i < savedNoteBook.getWeapons().size(); i++) {
            for (int j = 0; j < savedNoteBook.getNumberOfColumns(); j++) {
                UpdateNoteColumnDto dto = new UpdateNoteColumnDto(
                    savedNoteBook.getNoteBookId(),
                    CardType.WEAPON,
                    i,
                    j,
                    NotationSymbol.CROSS
                );

                mockMvc.perform(put("/api/notes/update/column")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

                NoteBook retrievedNb = noteBookRepository.findById(savedNoteBook.getNoteBookId()).get();
                Assert.assertNotNull(retrievedNb);
                matchColumnSymbolOrFail(retrievedNb, NotationSymbol.CROSS, CardType.WEAPON, i, j);
            }
        }
    }

    @Test
    public void updateAllRoomsColumnLinesTest() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        for (int i = 0; i < savedNoteBook.getRooms().size(); i++) {
            for (int j = 0; j < savedNoteBook.getNumberOfColumns(); j++) {
                UpdateNoteColumnDto dto = new UpdateNoteColumnDto(
                    savedNoteBook.getNoteBookId(),
                    CardType.ROOM,
                    i,
                    j,
                    NotationSymbol.CROSS
                );

                mockMvc.perform(put("/api/notes/update/column")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

                NoteBook retrievedNb = noteBookRepository.findById(savedNoteBook.getNoteBookId()).get();
                Assert.assertNotNull(retrievedNb);
                matchColumnSymbolOrFail(retrievedNb, NotationSymbol.CROSS, CardType.ROOM, i, j);
            }
        }
    }

    @Test
    public void updateColumnAllSymbolsTest() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        for (NotationSymbol symbol : NotationSymbol.values()) {
            UpdateNoteColumnDto dto = new UpdateNoteColumnDto(
                savedNoteBook.getNoteBookId(),
                CardType.CHARACTER,
                0,
                0,
                symbol
            );

            mockMvc.perform(put("/api/notes/update/column")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
            )
            .andExpect(status().isNoContent());

            NoteBook retrievedNb = noteBookRepository.findById(savedNoteBook.getNoteBookId()).get();
            Assert.assertNotNull(retrievedNb);
            matchColumnSymbolOrFail(retrievedNb, symbol, CardType.CHARACTER, 0, 0);
        }
    }

    @Test
    public void checkAllCharacterLinesNotCrossedTest() throws Exception {
        savedNoteBook.getCharacters().forEach((line) -> Assert.assertFalse(line.isCrossed()));
        savedNoteBook.getWeapons().forEach((line) -> Assert.assertFalse(line.isCrossed()));
        savedNoteBook.getRooms().forEach((line) -> Assert.assertFalse(line.isCrossed()));
    }

    @Test
    public void updateAllLinesCrossedTest() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        for (int i = 0; i < savedNoteBook.getCharacters().size(); i++) {
            crossLine(CardType.CHARACTER, i);
        }
        savedNoteBook.getCharacters().forEach((line) -> Assert.assertTrue(line.isCrossed()));

        for (int i = 0; i < savedNoteBook.getWeapons().size(); i++) {
            crossLine(CardType.WEAPON, i);
        }
        savedNoteBook.getWeapons().forEach((line) -> Assert.assertTrue(line.isCrossed()));

        for (int i = 0; i < savedNoteBook.getRooms().size(); i++) {
            crossLine(CardType.ROOM, i);
        }
        savedNoteBook.getRooms().forEach((line) -> Assert.assertTrue(line.isCrossed()));
    }

    @Test
    public void updateLineInexistingNotebook() throws Exception {
        MockSecurityContext.mockNormalUser(player.getUserId());

        UpdateNoteLineDto dto = new UpdateNoteLineDto(
                0,
                CardType.CHARACTER,
                0,
                true
        );

        MvcResult result = mockMvc.perform(put("/api/notes/update/line")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isBadRequest())
        .andReturn();

        String response = result.getResponse().getContentAsString();
        ApiError errorObj = objectMapper.readValue(response, ApiError.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, errorObj.getHttpStatus());
        Assert.assertEquals("NOTEBOOK_NOT_FOUND", errorObj.getMessage());
    }

    //#region Helpers
    private void matchColumnSymbolOrFail(NoteBook nb, NotationSymbol expectedSymbol, CardType type, int line, int column) {
        NotationSymbol notationSymbol;

        switch (type) {
            case CHARACTER:
                notationSymbol = nb.getCharacters().get(line).getColumns().get(column);
                break;
            case WEAPON:
                notationSymbol = nb.getWeapons().get(line).getColumns().get(column);
                break;
            case ROOM:
                notationSymbol = nb.getRooms().get(line).getColumns().get(column);
                break;
            default:
                fail("Unknown cardType specified");
                return;
        }

        Assert.assertEquals(expectedSymbol, notationSymbol);
    }

    private void crossLine(CardType type, int line) throws Exception {
        UpdateNoteLineDto dto = new UpdateNoteLineDto(
            savedNoteBook.getNoteBookId(),
            type,
            line,
            true
        );

        mockMvc.perform(put("/api/notes/update/line")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
        .andExpect(status().isNoContent());
    }
    //#endregion
}