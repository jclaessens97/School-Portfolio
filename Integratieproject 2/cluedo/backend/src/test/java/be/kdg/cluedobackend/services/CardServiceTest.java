package be.kdg.cluedobackend.services;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.helpers.EnumUtils;
import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.CharacterCard;
import be.kdg.cluedobackend.model.cards.RoomCard;
import be.kdg.cluedobackend.model.cards.WeaponCard;
import be.kdg.cluedobackend.model.cards.types.CardType;
import be.kdg.cluedobackend.model.cards.types.CharacterType;
import be.kdg.cluedobackend.model.cards.types.RoomType;
import be.kdg.cluedobackend.model.cards.types.WeaponType;
import be.kdg.cluedobackend.model.game.Scene;
import be.kdg.cluedobackend.model.game.SceneType;
import be.kdg.cluedobackend.model.users.Player;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.CardRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CardServiceTest {
    @Autowired
    private CardService cardService;

    @MockBean
    private CardRepository cardRepository;

    private List<Card> deck;
    private List<Player> threePlayers;
    private List<Player> fourPlayers;
    private List<Player> fivePlayers;
    private List<Player> sixPlayers;

    @Before
    public  void setUp() throws Exception {
        initializeDeck();
        initializePlayers();

        when(cardRepository.findAllByCardType(CardType.CHARACTER))
                .thenReturn(deck.stream().filter(c -> c.getCardType() == CardType.CHARACTER).collect(Collectors.toList()));
        when(cardRepository.findAllByCardType(CardType.WEAPON))
                .thenReturn(deck.stream().filter(c -> c.getCardType() == CardType.WEAPON).collect(Collectors.toList()));
        when(cardRepository.findAllByCardType(CardType.ROOM))
                .thenReturn(deck.stream().filter(c -> c.getCardType() == CardType.ROOM).collect(Collectors.toList()));
    }

    @Test
    public void createSolutionSceneTest() throws CluedoException {
        Scene solutionFile = cardService.createSolutionScene();

        Assert.assertEquals(3, solutionFile.getAllCards().size());
        Assert.assertNotNull(solutionFile.getCharacterCard());
        Assert.assertNotNull(solutionFile.getWeaponCard());
        Assert.assertNotNull(solutionFile.getRoomCard());
        Assert.assertEquals(solutionFile.getSceneType(), SceneType.SOLUTION);
    }

    @Test
    public void dealCardsToThreePlayersTest() throws CluedoException {
        when(cardRepository.findAll()).thenReturn(deck);

        Scene solutionFile = cardService.createSolutionScene();

        cardService.dealCardsToPlayers(threePlayers, solutionFile);

        Set<Card> dealtCards = new HashSet<>();
        threePlayers.forEach((p) -> {
            Assert.assertEquals(6, p.getCardsInHand().size());
            dealtCards.addAll(p.getCardsInHand());
        });

        Assert.assertEquals(18, dealtCards.size());
    }

    @Test
    public void dealCardsToFourPlayersTest() throws CluedoException {
        when(cardRepository.findAll()).thenReturn(deck);

        Scene solutionFile = cardService.createSolutionScene();

        cardService.dealCardsToPlayers(fourPlayers, solutionFile);

        Set<Card> dealtCards = new HashSet<>();

        Assert.assertEquals(5, fourPlayers.get(0).getCardsInHand().size());
        Assert.assertEquals(5, fourPlayers.get(1).getCardsInHand().size());
        Assert.assertEquals(4, fourPlayers.get(2).getCardsInHand().size());
        Assert.assertEquals(4, fourPlayers.get(3).getCardsInHand().size());

        dealtCards.addAll(fourPlayers.get(0).getCardsInHand());
        dealtCards.addAll(fourPlayers.get(1).getCardsInHand());
        dealtCards.addAll(fourPlayers.get(2).getCardsInHand());
        dealtCards.addAll(fourPlayers.get(3).getCardsInHand());
        Assert.assertEquals(18, dealtCards.size());
    }

    @Test
    public void dealCardsToFivePlayersTest() throws CluedoException {
        when(cardRepository.findAll()).thenReturn(deck);

        Scene solutionFile = cardService.createSolutionScene();

        cardService.dealCardsToPlayers(fivePlayers, solutionFile);

        Set<Card> dealtCards = new HashSet<>();

        Assert.assertEquals(4, fivePlayers.get(0).getCardsInHand().size());
        Assert.assertEquals(4, fivePlayers.get(1).getCardsInHand().size());
        Assert.assertEquals(4, fivePlayers.get(2).getCardsInHand().size());
        Assert.assertEquals(3, fivePlayers.get(3).getCardsInHand().size());
        Assert.assertEquals(3, fivePlayers.get(4).getCardsInHand().size());

        dealtCards.addAll(fivePlayers.get(0).getCardsInHand());
        dealtCards.addAll(fivePlayers.get(1).getCardsInHand());
        dealtCards.addAll(fivePlayers.get(2).getCardsInHand());
        dealtCards.addAll(fivePlayers.get(3).getCardsInHand());
        dealtCards.addAll(fivePlayers.get(4).getCardsInHand());
        Assert.assertEquals(18, dealtCards.size());
    }

    @Test
    public void dealCardsToSixPlayersTest() throws CluedoException {
        when(cardRepository.findAll()).thenReturn(deck);

        Scene solutionFile = cardService.createSolutionScene();

        cardService.dealCardsToPlayers(sixPlayers, solutionFile);

        Set<Card> dealtCards = new HashSet<>();
        sixPlayers.forEach((p) -> {
            Assert.assertEquals(3, p.getCardsInHand().size());
            dealtCards.addAll(p.getCardsInHand());
        });

        Assert.assertEquals(18, dealtCards.size());
    }

    //#region Helpers
    private void initializeDeck() {
        deck = new ArrayList<>();

        for (CharacterType character : CharacterType.values()) {
            deck.add(new CharacterCard(character, ""));
        }

        for (WeaponType weapon : WeaponType.values()) {
            deck.add(new WeaponCard(weapon, ""));
        }

        for (RoomType room : RoomType.values()) {
            if (room != RoomType.CELLAR) {
                deck.add(new RoomCard(room, ""));
            }
        }
    }

    private void initializePlayers() {
        String[] names = {
                "Ivaylo",
                "Dries",
                "Brecht",
                "Jeroen",
                "Jens",
                "Arne"
        };

        ArrayList<CharacterType> characterTypes = new ArrayList<>(EnumUtils.getEnumValues(CharacterType.class));

        threePlayers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Player p = new Player();
            User u = new User(UUID.randomUUID(), names[i], List.of(Role.USER));
            p.setUser(u);
            p.setCharacterType(characterTypes.remove(0));
            threePlayers.add(p);
        }

        fourPlayers = new ArrayList<>(threePlayers);
        Player p4 = new Player();
        User u4 = new User(UUID.randomUUID(), names[3], List.of(Role.USER));
        p4.setUser(u4);
        p4.setCharacterType(characterTypes.remove(0));
        fourPlayers.add(p4);

        fivePlayers = new ArrayList<>(fourPlayers);
        Player p5 = new Player();
        User u5 = new User(UUID.randomUUID(), names[4], List.of(Role.USER));
        p5.setUser(u5);
        p5.setCharacterType(characterTypes.remove(0));
        fivePlayers.add(p5);

        sixPlayers = new ArrayList<>(fivePlayers);
        Player p6 = new Player();
        User u6 = new User(UUID.randomUUID(), names[5], List.of(Role.USER));
        p6.setUser(u6);
        p6.setCharacterType(characterTypes.remove(0));
        sixPlayers.add(p6);
    }
    //#endregion
}
