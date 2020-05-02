package be.kdg.cluedobackend.dto.suggestion;

import be.kdg.cluedobackend.dto.CardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SceneDto {
    private boolean isAccusation;
    private List<CardDto> cards;
}
