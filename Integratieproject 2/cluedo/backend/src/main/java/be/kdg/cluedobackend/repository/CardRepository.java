package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.cards.Card;
import be.kdg.cluedobackend.model.cards.types.CardType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findAllByCardType(CardType cardType);
}
