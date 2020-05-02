package be.kdg.cluedobackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatisticDto {
    private Integer wins;
    private Integer losses;
    private Integer amountOfTurns;
    private Integer rightAccusations;
    private Integer wrongAccusations;
    private Double winsRatio;
    private Double accusationRatio;
}
