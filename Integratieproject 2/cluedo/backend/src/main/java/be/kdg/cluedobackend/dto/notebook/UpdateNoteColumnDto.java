package be.kdg.cluedobackend.dto.notebook;

import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.notebook.NotationSymbol;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoteColumnDto {
    private int notebookId;
    private CardType cardType;
    private int line;
    private int column;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NotationSymbol notationSymbol;
}
