package by.innowise.poverov.service;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;

import java.util.List;

public interface CardService {

    CardReadDto saveCard(CardWriteDto dto);
    CardReadDto findCardById(Long id);
    CardReadDto findAllCardByIdIn(List<Long> ids);
    int deleteCardById(Long id);
    CardReadDto updateCard(CardWriteDto dto);
}
