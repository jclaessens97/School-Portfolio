package be.kdg.cluedobackend.model.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@Entity
@Table
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsId;

    @Column
    private Float wins;

    @Column
    private Float losses;

    @Column
    private Integer amountOfTurns;

    @Column
    private Float rightAccusations;

    @Column
    private Float wrongAccusations;

    @Transient
    private float winsRatio;

    @Transient
    private float accusationRatio;

    @PostLoad
    private void postLoad() {
        if (this.losses == 0) {
            this.winsRatio = 0;
        } else {
            this.winsRatio = wins / losses;
        }

        if (this.wrongAccusations == 0) {
            this.accusationRatio = 0;
        } else {
            this.accusationRatio = rightAccusations / wrongAccusations;
        }
    }

    public GameStatistics() {
        this.wins = 0f;
        this.losses = 0f;
        this.amountOfTurns = 0;
        this.rightAccusations = 0f;
        this.wrongAccusations= 0f;
    }
}
