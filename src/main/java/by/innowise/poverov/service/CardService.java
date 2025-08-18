package by.innowise.poverov.service;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;

import java.util.List;

public interface CardService {

    CardReadDto saveCard(CardWriteDto dto);
    CardReadDto findCardById(Long id);
    List<CardReadDto> findAllCardsByIds(List<Long> ids);
    CardReadDto updateCard(Long id, CardWriteDto dto);
    CardReadDto deleteCardById(Long id);
}
