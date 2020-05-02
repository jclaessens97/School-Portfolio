package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.config.CluedoProperties;
import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.model.cards.CharacterCard;
import be.kdg.cluedobackend.model.cards.RoomCard;
import be.kdg.cluedobackend.model.cards.WeaponCard;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.cards.types.WeaponType;
import be.kdg.cluedobackend.model.game.Cluedo;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.game.SceneType;
import be.kdg.cluedobackend.model.game.SuggestionType;
import be.kdg.cluedobackend.model.users.GameStatistics;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CluedoRepository;
import be.kdg.cluedobackend.repository.PlayerRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GameServiceTest {
    @Autowired
    private GameService gameService;

    @MockBean
    private CluedoRepository cluedoRepository;

    @MockBean
    private PlayerRepository playerRepository;

    @Autowired
    private CluedoProperties cluedoProperties;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void makeSuggestionTest() throws CluedoException {
        List<Player> threePlayers = new ArrayList<>();

        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setGameStatistics(new GameStatistics());

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setGameStatistics(new GameStatistics());

        User user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setGameStatistics(new GameStatistics());

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        threePlayers.add(new Player(user1, cluedo, CharacterType.BLUE));
        threePlayers.add(new Player(user2, cluedo, CharacterType.GREEN));
        threePlayers.add(new Player(user3, cluedo, CharacterType.PURPLE));
        threePlayers.get(0).getCardsInHand().add(new CharacterCard(CharacterType.BLUE, ""));
        threePlayers.get(0).getCardsInHand().add(new WeaponCard(WeaponType.DAGGER, ""));
        threePlayers.get(0).setGame_order(0);
        threePlayers.get(1).getCardsInHand().add(new WeaponCard(WeaponType.REVOLVER, ""));
        threePlayers.get(1).getCardsInHand().add(new RoomCard(RoomType.BALLROOM, ""));
        threePlayers.get(1).setGame_order(1);
        threePlayers.get(2).getCardsInHand().add(new WeaponCard(WeaponType.ROPE, ""));
        threePlayers.get(2).getCardsInHand().add(new RoomCard(RoomType.BILLIARDROOM,""));
        threePlayers.get(2).setGame_order(2);

        cluedo.setPlayers(threePlayers);
        cluedo.switchTurn();

        int gameId = 0;
        when(playerRepository.findAllByCluedo_CluedoId(gameId)).thenReturn(threePlayers);

        Scene solution = new Scene(
                new CharacterCard(CharacterType.BLUE, ""),
                new WeaponCard(WeaponType.CANDLESTICK,""),
                new RoomCard(RoomType.BALLROOM,""),
                SceneType.SOLUTION
        );
        cluedo.setCaseFile(solution);

        Scene scene = new Scene(
            new CharacterCard(CharacterType.BLUE,""),
            new WeaponCard(WeaponType.CANDLESTICK,""),
            new RoomCard(RoomType.BALLROOM, ""), SceneType.SUGGESTION
        );

        when(cluedoRepository.findById(gameId)).thenReturn(Optional.of(cluedo));

        Map<SuggestionType, Player> suggestion = gameService.makeSuggestion(gameId, scene, new ArrayList<>());

        Assert.assertFalse(suggestion.isEmpty());
        Assert.assertEquals(threePlayers.get(0).getCharacterType(), suggestion.get(SuggestionType.ASKING).getCharacterType());
        Assert.assertEquals(threePlayers.get(1).getCharacterType(), suggestion.get(SuggestionType.RESPONDING).getCharacterType());
    }

    @Test
    public void makeSuggestionNoMatchTest() throws CluedoException {
        List<Player> threePlayers = new ArrayList<>();

        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setGameStatistics(new GameStatistics());

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setGameStatistics(new GameStatistics());

        User user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setGameStatistics(new GameStatistics());

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        threePlayers.add(new Player(user1, cluedo, CharacterType.BLUE));
        threePlayers.add(new Player(user2, cluedo, CharacterType.GREEN));
        threePlayers.add(new Player(user3, cluedo, CharacterType.PURPLE));
        threePlayers.get(0).getCardsInHand().add(new CharacterCard(CharacterType.BLUE,""));
        threePlayers.get(0).getCardsInHand().add(new WeaponCard(WeaponType.DAGGER, ""));
        threePlayers.get(0).setGame_order(0);
        threePlayers.get(1).getCardsInHand().add(new WeaponCard(WeaponType.REVOLVER, ""));
        threePlayers.get(1).getCardsInHand().add(new RoomCard(RoomType.BALLROOM, ""));
        threePlayers.get(1).setGame_order(1);
        threePlayers.get(2).getCardsInHand().add(new WeaponCard(WeaponType.ROPE, ""));
        threePlayers.get(2).getCardsInHand().add(new RoomCard(RoomType.BILLIARDROOM,""));
        threePlayers.get(2).setGame_order(2);

        cluedo.setPlayers(threePlayers);
        cluedo.switchTurn();

        int gameId = 0;
        when(playerRepository.findAllByCluedo_CluedoId(gameId)).thenReturn(threePlayers);

        Scene solution = new Scene(
                new CharacterCard(CharacterType.BLUE, ""),
                new WeaponCard(WeaponType.CANDLESTICK,""),
                new RoomCard(RoomType.BALLROOM, ""),
                SceneType.SOLUTION
        );
        cluedo.setCaseFile(solution);

        Scene scene = new Scene(
            new CharacterCard(CharacterType.RED,""),
            new WeaponCard(WeaponType.CANDLESTICK,""),
            new RoomCard(RoomType.KITCHEN,""),
            SceneType.SUGGESTION
        );

        when(cluedoRepository.findById(gameId)).thenReturn(Optional.of(cluedo));

        Map<SuggestionType, Player> suggestion = gameService.makeSuggestion(gameId, scene, new ArrayList<>());

        Assert.assertEquals(0, suggestion.size());
    }

    @Test
    public void makeTrueAccusationTest() throws CluedoException {
        List<Player> threePlayers = new ArrayList<>();

        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setGameStatistics(new GameStatistics());

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setGameStatistics(new GameStatistics());

        User user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setGameStatistics(new GameStatistics());

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        threePlayers.add(new Player(user1, cluedo, CharacterType.BLUE));
        threePlayers.add(new Player(user2, cluedo, CharacterType.GREEN));
        threePlayers.add(new Player(user3, cluedo, CharacterType.PURPLE));
        threePlayers.get(0).getCardsInHand().add(new CharacterCard(CharacterType.BLUE,""));
        threePlayers.get(0).getCardsInHand().add(new WeaponCard(WeaponType.DAGGER, ""));
        threePlayers.get(0).setGame_order(0);
        threePlayers.get(1).getCardsInHand().add(new WeaponCard(WeaponType.REVOLVER, ""));
        threePlayers.get(1).getCardsInHand().add(new RoomCard(RoomType.BALLROOM, ""));
        threePlayers.get(1).setGame_order(1);
        threePlayers.get(2).getCardsInHand().add(new WeaponCard(WeaponType.ROPE, ""));
        threePlayers.get(2).getCardsInHand().add(new RoomCard(RoomType.BILLIARDROOM,""));
        threePlayers.get(2).setGame_order(2);

        cluedo.setPlayers(threePlayers);
        cluedo.switchTurn();

        final int gameId = 0;

        Scene solution = new Scene(
            new CharacterCard(CharacterType.BLUE, ""),
            new WeaponCard(WeaponType.CANDLESTICK, ""),
            new RoomCard(RoomType.BALLROOM, ""),
            SceneType.SOLUTION
        );

        cluedo.setCaseFile(solution);

        when(cluedoRepository.findById(gameId)).thenReturn(Optional.of(cluedo));

        Scene accusation = new Scene(
            new CharacterCard(CharacterType.BLUE, ""),
            new WeaponCard(WeaponType.CANDLESTICK,""),
            new RoomCard(RoomType.BALLROOM, ""),
            SceneType.ACCUSATION
        );

        boolean accusationResult = gameService.makeAccusation(gameId, accusation, new ArrayList<>())[0];

        Assert.assertTrue(accusationResult);
    }

    @Test
    public void makeFalseAccusationTest() throws CluedoException {
        List<Player> threePlayers = new ArrayList<>();

        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setGameStatistics(new GameStatistics());

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setGameStatistics(new GameStatistics());

        User user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setGameStatistics(new GameStatistics());

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        threePlayers.add(new Player(user1, cluedo, CharacterType.BLUE));
        threePlayers.add(new Player(user2, cluedo, CharacterType.GREEN));
        threePlayers.add(new Player(user3, cluedo, CharacterType.PURPLE));
        threePlayers.get(0).getCardsInHand().add(new CharacterCard(CharacterType.BLUE,""));
        threePlayers.get(0).getCardsInHand().add(new WeaponCard(WeaponType.DAGGER, ""));
        threePlayers.get(0).setGame_order(0);
        threePlayers.get(1).getCardsInHand().add(new WeaponCard(WeaponType.REVOLVER, ""));
        threePlayers.get(1).getCardsInHand().add(new RoomCard(RoomType.BALLROOM, ""));
        threePlayers.get(1).setGame_order(1);
        threePlayers.get(2).getCardsInHand().add(new WeaponCard(WeaponType.ROPE, ""));
        threePlayers.get(2).getCardsInHand().add(new RoomCard(RoomType.BILLIARDROOM,""));
        threePlayers.get(2).setGame_order(2);

        cluedo.setPlayers(threePlayers);
        cluedo.switchTurn();

        final int gameId = 0;

        Scene solution = new Scene(
            new CharacterCard(CharacterType.BLUE, ""),
            new WeaponCard(WeaponType.CANDLESTICK, ""),
            new RoomCard(RoomType.BALLROOM,""),
            SceneType.SOLUTION
        );

        cluedo.setCaseFile(solution);

        when(cluedoRepository.findById(gameId)).thenReturn(Optional.of(cluedo));

        Scene accusation = new Scene(
            new CharacterCard(CharacterType.RED, ""),
            new WeaponCard(WeaponType.CANDLESTICK, ""),
            new RoomCard(RoomType.BALLROOM, ""),
            SceneType.ACCUSATION
        );

        boolean accusationResult = gameService.makeAccusation(gameId, accusation, new ArrayList<>())[0];

        Assert.assertFalse(accusationResult);
    }

    @Test
    public void checkHasEnded() throws CluedoException {
        List<Player> threePlayers = new ArrayList<>();

        User user1 = new User();
        user1.setUserId(UUID.randomUUID());
        user1.setGameStatistics(new GameStatistics());

        User user2 = new User();
        user2.setUserId(UUID.randomUUID());
        user2.setGameStatistics(new GameStatistics());

        User user3 = new User();
        user3.setUserId(UUID.randomUUID());
        user3.setGameStatistics(new GameStatistics());

        Cluedo cluedo = new Cluedo(cluedoProperties.getTurnDuration(), cluedoProperties.getMaxPlayers());
        threePlayers.add(new Player(user1, cluedo, CharacterType.BLUE));
        threePlayers.add(new Player(user2, cluedo, CharacterType.GREEN));
        threePlayers.add(new Player(user3, cluedo, CharacterType.PURPLE));
        threePlayers.get(0).getCardsInHand().add(new CharacterCard(CharacterType.BLUE,""));
        threePlayers.get(0).getCardsInHand().add(new WeaponCard(WeaponType.DAGGER, ""));
        threePlayers.get(0).setGame_order(0);
        threePlayers.get(1).getCardsInHand().add(new WeaponCard(WeaponType.REVOLVER, ""));
        threePlayers.get(1).getCardsInHand().add(new RoomCard(RoomType.BALLROOM, ""));
        threePlayers.get(1).setGame_order(1);
        threePlayers.get(2).getCardsInHand().add(new WeaponCard(WeaponType.ROPE, ""));
        threePlayers.get(2).getCardsInHand().add(new RoomCard(RoomType.BILLIARDROOM,""));
        threePlayers.get(2).setGame_order(2);
        threePlayers.get(2).setActive(false);

        cluedo.setPlayers(threePlayers);
        cluedo.switchTurn();

        final int gameId = 0;

        Scene solution = new Scene(
                new CharacterCard(CharacterType.BLUE, ""),
                new WeaponCard(WeaponType.CANDLESTICK, ""),
                new RoomCard(RoomType.BALLROOM,""),
                SceneType.SOLUTION
        );

        cluedo.setCaseFile(solution);

        when(cluedoRepository.findById(gameId)).thenReturn(Optional.of(cluedo));

        Scene accusation = new Scene(
                new CharacterCard(CharacterType.RED, ""),
                new WeaponCard(WeaponType.CANDLESTICK, ""),
                new RoomCard(RoomType.BALLROOM, ""),
                SceneType.ACCUSATION
        );

        boolean hasEnded = gameService.makeAccusation(gameId, accusation, new ArrayList<>())[1];
        Assert.assertTrue(hasEnded);
    }
    //#endregion

}
