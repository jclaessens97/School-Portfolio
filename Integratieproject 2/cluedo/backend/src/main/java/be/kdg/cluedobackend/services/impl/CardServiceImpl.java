package be.kdg.cluedobackend.services.impl;

import be.kdg.cluedobackend.exceptions.CluedoException;
import be.kdg.cluedobackend.exceptions.CluedoExceptionType;
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
import be.kdg.cluedobackend.repository.CardRepository;
import be.kdg.cluedobackend.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class CardServiceImpl implements CardService {
    private CardRepository cardRepository;
    private final String CARD_PATH = "assets/img/cards/";


    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        initializeDefault();
    }

    @Override
    public Scene createSolutionScene() throws CluedoException {
        final CharacterCard characterCard = (CharacterCard) drawRandomCardFromDeck(CardType.CHARACTER);
        final WeaponCard weaponCard = (WeaponCard) drawRandomCardFromDeck(CardType.WEAPON);
        final RoomCard roomCard = (RoomCard) drawRandomCardFromDeck(CardType.ROOM);
        return new Scene(characterCard, weaponCard, roomCard, SceneType.SOLUTION);
    }

    @Override
    public void dealCardsToPlayers(List<Player> joinedPlayers, Scene caseFile) {
        List<Card> cardsToDeal = this.cardRepository.findAll();

        cardsToDeal.removeAll(List.of(caseFile.getCharacterCard(), caseFile.getWeaponCard(), caseFile.getRoomCard()));
        while (!cardsToDeal.isEmpty()) {
            for (Player joinedPlayer : joinedPlayers) {
                if (cardsToDeal.isEmpty()) break;
                int randomIndex = ThreadLocalRandom.current().nextInt(cardsToDeal.size());
                Card randomCard = cardsToDeal.remove(randomIndex);
                joinedPlayer.getCardsInHand().add(randomCard);
            }
        }
    }

    //#region CRUD
    @Override
    public void initializeDefault() {
        if (this.cardRepository.count() > 0) return;

        Set<Card> deck = new HashSet<>();

        for (CharacterType character : CharacterType.values()) {
            deck.add(new CharacterCard(character, CARD_PATH + character.name().toLowerCase() + ".png"));
        }

        for (WeaponType weapon : WeaponType.values()) {
            deck.add(new WeaponCard(weapon, CARD_PATH + weapon.name().toLowerCase() + ".png"));
        }

        for (RoomType room : RoomType.values()) {
            if (room != RoomType.CELLAR) {
                deck.add(new RoomCard(room, CARD_PATH + room.name().toLowerCase() + ".png"));
            }
        }

        this.cardRepository.saveAll(deck);
        for (CharacterType character : CharacterType.values()) {
            deck.add(new CharacterCard(character, CARD_PATH + character.name().toLowerCase() + ".png"));
        }

        for (WeaponType weapon : WeaponType.values()) {
            deck.add(new WeaponCard(weapon, CARD_PATH + weapon.name().toLowerCase() + ".png"));
        }

        for (RoomType room : RoomType.values()) {
            if (room != RoomType.CELLAR) {
                deck.add(new RoomCard(room, CARD_PATH + room.name().toLowerCase() + ".png"));
            }
        }

        this.cardRepository.saveAll(deck);
    }

    @Override
    public Card getCardById(int id) throws CluedoException {
        return cardRepository.findById(id)
                .orElseThrow(() ->
                        new CluedoException(CluedoExceptionType.CARD_NOT_FOUND)
                );
    }

    @Override
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }
    //#endregion

    //#region Helpers
    private Card drawRandomCardFromDeck(CardType category) throws CluedoException {
        List<Card> categoryCards = this.cardRepository.findAllByCardType(category);

        if (categoryCards == null) {
            throw new CluedoException(CluedoExceptionType.CATEGORY_NOT_EXIST,
                    String.format("Category %s does not exist in deck", category)
            );
        }

        return categoryCards.get(ThreadLocalRandom.current().nextInt(categoryCards.size()));
    }
    //#endregion
}