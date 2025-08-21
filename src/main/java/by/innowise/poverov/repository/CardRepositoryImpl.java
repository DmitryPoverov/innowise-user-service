package by.innowise.poverov.repository;

import by.innowise.poverov.entity.Card;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepositoryImpl implements CardCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Card updateCard(Long id, Card card) {
        Card cardFromDB = entityManager.find(Card.class, id);
        if (cardFromDB != null) {
            cardFromDB.setNumber(card.getNumber());
            cardFromDB.setHolder(card.getHolder());
            cardFromDB.setExpirationDate(card.getExpirationDate());
            return cardFromDB;
        }
        return null;
    }
}
