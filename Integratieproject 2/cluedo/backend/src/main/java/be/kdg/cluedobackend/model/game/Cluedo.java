package be.kdg.cluedobackend.model.game;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
import be.kdg.cluedobackend.model.chat.Message;
import be.kdg.cluedobackend.model.gameboard.GameBoard;
import be.kdg.cluedobackend.model.users.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table
public class Cluedo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cluedoId;

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Scene caseFile;

    @Transient
    private GameBoard gameBoard;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "cluedo")
    private List<Player> players;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn
    private List<Turn> turns;

    private boolean active;
    @OneToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Message> messages;

    private boolean won = false;
    //#region  Lobby settings
    private String lobbyName;
    @OneToOne(cascade=CascadeType.REMOVE, optional=true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Player host;
    private Integer turnDuration; // Minutes
    private Integer maxPlayers;
    //#endregion

    public Cluedo() {
        this.players = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.turns = new ArrayList<>();
        active = false;
    }

    public Cluedo(int turnDuration, int maxPlayers) {
        this();
        this.turnDuration = turnDuration;
        this.maxPlayers = maxPlayers;
    }

    public void assignNewHost() { host = this.players.get(0); }

    public void joinPlayer(Player player) { this.players.add(player); }

    public void setHost(Player player) {
        this.players.add(player);
        this.host = player;
        this.lobbyName = String.format("%s' lobby", player.getUser().getUserName());
    }

    public void switchTurn() throws CluedoException {
        //no current turn --> 0
        //else (currentTurn + 1) % playerSize           (5 players --> (4 + 1) % 5 = 0)
        int nextPlayer = (getCurrentTurn() == null ? 0 : (getCurrentTurn().getPlayer().getGame_order()+ 1) % players.size());

        //make comparator to compare playerOrders
        Comparator<Player> pOrder = Comparator.comparing(Player::getGame_order);

        //filter all inactive players out of the list (players who got kicked or made false accusation)
        //then get next player with order >= next order AND the lowest active player
        List<Player> activePlayers =  this.players.stream().filter(Player::isActive).collect(Collectors.toList());
        Optional<Player> potentialNext = activePlayers.stream().filter(p ->p.getGame_order() >= nextPlayer).min(pOrder);
        Optional<Player> lowest = activePlayers.stream().min(pOrder);

        //take the next potential player OR the lowest active player OR throw an exception when no player is found
        Supplier<CluedoException> notfound = () -> new CluedoException(CluedoExceptionType.PLAYER_NOT_FOUND);
        this.turns.add(new Turn((potentialNext.orElse(lowest.orElseThrow(notfound))), LocalDateTime.now(), this.turnDuration));
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setCluedo(null);
    }

    public void setPlayerInactive(Player player) {player.setActive(false);}

    public Turn getCurrentTurn() {
        return this.turns.stream().max(Comparator.comparing(Turn::getStartTurn)).orElse(null);
    }

    public boolean isPlayerInGame(Player player) { return this.players.contains(player); }

    public boolean isHost(Player player) { return this.host.equals(player); }

    public boolean isFull() { return players.size() >= this.maxPlayers; }

    public boolean hasPlayersLeft() { return !this.players.isEmpty(); }

    /**
     * Creates list with numbers from 0 to n players
     * Then shuffles the list and gives every player a turn number.
     */
    public void determinePlayerOrder() {
        Integer[] tmpTurns = new Integer[players.size()];
        Arrays.parallelSetAll(tmpTurns, i -> i > players.size() ? 0 : i);
        LinkedList<Integer> turnOrders = new LinkedList<>(Arrays.asList(tmpTurns));
        Collections.shuffle(turnOrders);
        players.forEach(p -> p.setGame_order(turnOrders.removeFirst()));
    }
}
