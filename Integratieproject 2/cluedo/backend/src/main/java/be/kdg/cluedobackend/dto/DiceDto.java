package be.kdg.cluedobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DiceDto {
    private int roll1;
    private int roll2;

    public DiceDto(int roll1) {
        this.roll1 = roll1;
        this.roll2 = 0;
    }

    public DiceDto(int[] rolls){
        this.roll1 = rolls[0];
        this.roll2 = rolls[1];
    }
}
