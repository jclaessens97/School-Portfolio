package be.kdg.cluedobackend.dto.notebook;

import be.kdg.cluedobackend.model.notebook.NoteBook;
import be.kdg.cluedobackend.model.notebook.NoteLine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotebookDto {
    private Integer notebookId;
    private List<NoteLine> characters;
    private List<NoteLine> weapons;
    private List<NoteLine> rooms;

    public NotebookDto(NoteBook noteBook) {
        this.notebookId = noteBook.getNoteBookId();
        this.characters = noteBook.getCharacters();
        this.weapons = noteBook.getWeapons();
        this.rooms = noteBook.getRooms();
    }
}
