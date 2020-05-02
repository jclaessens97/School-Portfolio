package be.kdg.cluedobackend.unit;

import be.kdg.cluedobackend.config.CluedoProperties;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.users.Player;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CluedoTest {
    @Autowired
    private CluedoProperties cluedoProperties;
    private Cluedo game;

    @Before
    public void setup() {
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Player player = new Player();
            player.setPlayerId(i);
            player.setGame_order(i);
            players.add(player);
        }

        game = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        game.setPlayers(players);
    }

    @Test
    public void switchTurnTest() throws CluedoException {
        game.switchTurn();
        Assert.assertEquals(0, game.getCurrentTurn().getPlayer().getPlayerId().intValue());
    }

    @Test
    public void switchTurnAfterRemove() throws CluedoException {
        game.setPlayerInactive(game.getPlayers().stream().filter(p -> p.getPlayerId() == 0).findFirst().get());
        game.switchTurn();
        Assert.assertEquals(1, game.getCurrentTurn().getPlayer().getPlayerId().intValue());
    }

    @Test
    public void determinePlayerOrder() {
        List<Player> threePlayers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Player player = new Player();
            threePlayers.add(player);
        }

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        cluedo.setPlayers(threePlayers);

        cluedo.determinePlayerOrder();

        Assert.assertTrue(cluedo.getPlayers().get(0).getGame_order() > -1);
        Assert.assertTrue(cluedo.getPlayers().get(1).getGame_order() > -1);
        Assert.assertTrue(cluedo.getPlayers().get(1).getGame_order() > -1);
    }
}
