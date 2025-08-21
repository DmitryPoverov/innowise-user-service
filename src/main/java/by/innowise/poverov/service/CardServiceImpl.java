package by.innowise.poverov.service;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.entity.Card;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.mapper.CardMapper;
import by.innowise.poverov.repository.CardRepository;
import by.innowise.poverov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardServiceImpl implements CardService {

    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public CardReadDto saveCard(CardWriteDto writeDto) {
        Long userIdToSaveCard = writeDto.getUserId();
        if (userRepository.existsById(userIdToSaveCard)) {

            String numberFromDto = writeDto.getNumber();
            if (cardRepository.existsByNumber(numberFromDto)) {
                throw new EntityIsNotUniqueCustomException(numberFromDto);
            }

            Card card = cardMapper.toCard(writeDto);
            Card savedCard = cardRepository.save(card);
            return cardMapper.toCardReadDto(savedCard);
        }
        throw new EntityNotFoundCustomException(userIdToSaveCard);
    }


    @Override
    public CardReadDto findCardById(Long id) {
        return cardRepository.findCardById(id)
                .map(cardMapper::toCardReadDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(id));
    }


    @Override
    public List<CardReadDto> findAllCardsByIds(List<Long> ids) {
        return cardRepository.findAllById(ids)
                .stream()
                .map(cardMapper::toCardReadDto)
                .toList();
    }


    @Override
    @Transactional
    public CardReadDto updateCard(Long id, CardWriteDto cardWriteDto) {
        Long userIdToUpdateCard = cardWriteDto.getUserId();
        if (userRepository.existsById(userIdToUpdateCard)) {
            String newNumber = cardWriteDto.getNumber();

            Card cardFromDB = cardRepository.findCardById(id)
                    .orElseThrow(() -> new EntityNotFoundCustomException(newNumber));

            if (!Objects.equals(newNumber, cardFromDB.getNumber()) && cardRepository.existsByNumber(newNumber)) {
                throw new EntityIsNotUniqueCustomException(newNumber);
            }

            cardMapper.updateCardFromDto(cardWriteDto, cardFromDB);
            cardRepository.save(cardFromDB);
            return cardMapper.toCardReadDto(cardFromDB);
        }
        throw new EntityNotFoundCustomException(userIdToUpdateCard);
    }


    @Override
    @Transactional
    public void deleteCardById(Long id) {
        if (cardRepository.deleteCardById(id) == 0) {
            throw new EntityNotFoundCustomException(id);
        }
    }
}
