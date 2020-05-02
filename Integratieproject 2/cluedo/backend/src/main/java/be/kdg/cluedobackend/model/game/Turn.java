package be.kdg.cluedobackend.model.game;

import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.users.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Getter
@Setter
public class Turn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long turnId;
    @ManyToOne
    private Player player;
    private LocalDateTime startTurn;
    private int maxTurnTime;
    private Integer diceTotal;
    private Integer xCoord;
    private Integer yCoord;
    @OneToOne(cascade = CascadeType.ALL)
    private Scene scene;
    @ManyToOne
    private Player respondant;
    @ManyToOne(cascade = CascadeType.ALL)
   // @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Card shown;

    public Turn(Player player, LocalDateTime localDateTime, int maxTurnTime) {
        this.player = player;
        this.startTurn = localDateTime;
        this.maxTurnTime = maxTurnTime;
    }

    public void setXAndYCoord(int x, int y) {
        this.xCoord = x;
        this.yCoord = y;
    }

}
