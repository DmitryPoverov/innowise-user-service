package by.innowise.poverov.repository;

import by.innowise.poverov.entity.Card;

public interface CardCustomRepository {

    Card updateCard(Long id, Card card);
}
