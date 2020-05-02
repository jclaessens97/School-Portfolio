package be.kdg.cluedobackend.dto.notebook;

import be.kdg.cluedobackend.model.cards.types.CardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteLineDto {
    private int notebookId;
    private CardType cardType;
    private int line;
    private boolean crossed;
}
