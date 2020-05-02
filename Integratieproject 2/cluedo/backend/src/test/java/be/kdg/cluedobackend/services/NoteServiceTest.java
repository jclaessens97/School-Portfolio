package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.model.notebook.NoteBook;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class NoteServiceTest {
    @Autowired
    private NoteService noteService;

    @Test
    public void initializeNoteBookThreePlayersTest() {
        NoteBook generatedNotebook = noteService.initializeNoteBook(3);

        Assert.assertEquals(2, generatedNotebook.getNumberOfColumns());
        Assert.assertEquals(22, getNumberOfRows(generatedNotebook));
    }

    @Test
    public void initializeNoteBookFourPlayersTest() {
        NoteBook generatedNotebook = noteService.initializeNoteBook(4);

        Assert.assertEquals(3, generatedNotebook.getNumberOfColumns());
        Assert.assertEquals(22, getNumberOfRows(generatedNotebook));
    }

    @Test
    public void initializeNoteBookFivePlayersTest() {
        NoteBook generatedNotebook = noteService.initializeNoteBook(5);

        Assert.assertEquals(4, generatedNotebook.getNumberOfColumns());
        Assert.assertEquals(22, getNumberOfRows(generatedNotebook));
    }

    @Test
    public void initializeNoteBookSixPlayersTest() {
        NoteBook generatedNotebook = noteService.initializeNoteBook(6);

        Assert.assertEquals(5, generatedNotebook.getNumberOfColumns());
        Assert.assertEquals(22, getNumberOfRows(generatedNotebook));
    }

    //#region Helpers
    private int getNumberOfRows(NoteBook nb) {
        return nb.getCharacters().size()
            + nb.getWeapons().size()
            + nb.getRooms().size();
    }
    //#endregion
}
