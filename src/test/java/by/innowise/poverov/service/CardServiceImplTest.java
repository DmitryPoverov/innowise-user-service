package by.innowise.poverov.service;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.entity.Card;
import by.innowise.poverov.entity.User;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.mapper.CardMapper;
import by.innowise.poverov.repository.CardRepository;
import by.innowise.poverov.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    private static final Long ID_1L = 1L;
    private static final String CARD_NUMBER_1 = "1111222233334444";
    private static final String CARD_HOLDER_1 = "Holder Name";
    private static final LocalDate EXPIRATION_DATE_1 = LocalDate.of(2030, 1, 1);
    private static final Long ID_2L = 2L;
    private static final String CARD_NUMBER_2 = "5555666677778888";
    public static final String CACHE_NAME = "redis_cache_for_users";

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CardMapper cardMapper;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;

    @InjectMocks
    private CardServiceImpl cardServiceImpl;

    private Card cardWithoutId;
    private Card cardWithId1;
    private Card cardWithId2;
    private CardWriteDto cardWriteDto;
    private CardReadDto cardReadDto1;
    private CardReadDto cardReadDto2;
    private List<Long> cardIdsList;
    private List<Card> cardList;
    private List<CardReadDto> cardReadDtoList;

    @BeforeEach
    void setUp() {
        User user = User.builder().id(ID_1L).build();

        cardWithoutId = Card.builder()
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .user(user)
                .build();

        cardWithId1 = Card.builder()
                .id(ID_1L)
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .user(user)
                .build();

        cardWithId2 = Card.builder()
                .id(ID_2L)
                .number(CARD_NUMBER_2)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .user(user)
                .build();

        cardWriteDto = CardWriteDto.builder()
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .userId(ID_1L)
                .build();

        cardReadDto1 = CardReadDto.builder()
                .id(ID_1L)
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .userId(ID_1L)
                .build();

        cardReadDto2 = CardReadDto.builder()
                .id(ID_2L)
                .number(CARD_NUMBER_2)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE_1)
                .userId(ID_1L)
                .build();

        cardIdsList = List.of(ID_1L, ID_2L);
        cardList = List.of(cardWithId1, cardWithId2);
        cardReadDtoList = List.of(cardReadDto1, cardReadDto2);
    }

    @Test
    void saveCard_shouldReturnCardReadDto_whenDataIsValid() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(true);
        Mockito.when(cardRepository.existsByNumber(CARD_NUMBER_1)).thenReturn(false);
        Mockito.when(cardMapper.toCard(cardWriteDto)).thenReturn(cardWithoutId);
        Mockito.when(cardRepository.save(cardWithoutId)).thenReturn(cardWithId1);
        Mockito.when(cardMapper.toCardReadDto(cardWithId1)).thenReturn(cardReadDto1);

        CardReadDto actual = cardServiceImpl.saveCard(cardWriteDto);

        Assertions.assertEquals(cardReadDto1, actual);
        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verify(cardRepository).existsByNumber(CARD_NUMBER_1);
        Mockito.verify(cardMapper).toCard(cardWriteDto);
        Mockito.verify(cardRepository).save(cardWithoutId);
        Mockito.verify(cardMapper).toCardReadDto(cardWithId1);
        Mockito.verifyNoMoreInteractions(userRepository, cardRepository, cardMapper);
    }

    @Test
    void saveCard_shouldThrowException_whenUserNotFound() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> cardServiceImpl.saveCard(cardWriteDto));

        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verifyNoInteractions(cardRepository, cardMapper);
    }

    @Test
    void saveCard_shouldThrowException_whenCardNumberIsNotUnique() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(true);
        Mockito.when(cardRepository.existsByNumber(CARD_NUMBER_1)).thenReturn(true);

        Assertions.assertThrows(EntityIsNotUniqueCustomException.class, () -> cardServiceImpl.saveCard(cardWriteDto));

        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verify(cardRepository).existsByNumber(CARD_NUMBER_1);
        Mockito.verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void findCardById_shouldReturnCardReadDto_whenCardExists() {
        Mockito.when(cardRepository.findCardById(ID_1L)).thenReturn(Optional.of(cardWithId1));
        Mockito.when(cardMapper.toCardReadDto(cardWithId1)).thenReturn(cardReadDto1);

        CardReadDto actual = cardServiceImpl.findCardById(ID_1L);

        Assertions.assertEquals(cardReadDto1, actual);
        Mockito.verify(cardRepository).findCardById(ID_1L);
        Mockito.verify(cardMapper).toCardReadDto(cardWithId1);
        Mockito.verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void findCardById_shouldThrowException_whenCardNotFound() {
        Mockito.when(cardRepository.findCardById(ID_1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> cardServiceImpl.findCardById(ID_1L));

        Mockito.verify(cardRepository).findCardById(ID_1L);
        Mockito.verifyNoInteractions(cardMapper);
    }

    @Test
    void findAllCardsByIds_shouldReturnCardList_whenCardsExist() {
        Mockito.when(cardRepository.findAllById(cardIdsList)).thenReturn(cardList);
        Mockito.when(cardMapper.toCardReadDto(cardWithId1)).thenReturn(cardReadDto1);
        Mockito.when(cardMapper.toCardReadDto(cardWithId2)).thenReturn(cardReadDto2);

        List<CardReadDto> actual = cardServiceImpl.findAllCardsByIds(cardIdsList);

        Assertions.assertEquals(cardReadDtoList, actual);
        Mockito.verify(cardRepository).findAllById(cardIdsList);
        Mockito.verify(cardMapper).toCardReadDto(cardWithId1);
        Mockito.verify(cardMapper).toCardReadDto(cardWithId2);
        Mockito.verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void findAllCardsByIds_shouldReturnEmptyList_whenCardsDoNotExist() {
        Mockito.when(cardRepository.findAllById(cardIdsList)).thenReturn(Collections.emptyList());

        List<CardReadDto> actual = cardServiceImpl.findAllCardsByIds(cardIdsList);

        Assertions.assertTrue(actual.isEmpty());
        Mockito.verify(cardRepository).findAllById(cardIdsList);
        Mockito.verifyNoInteractions(cardMapper);
    }

    @Test
    void updateCard_shouldReturnUpdatedCardReadDto_whenDataIsValid() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(true);//
        Mockito.when(cardRepository.findCardById(ID_1L)).thenReturn(Optional.of(cardWithId1));
        Mockito.when(cardRepository.save(cardWithId1)).thenReturn(cardWithId1);
        Mockito.when(cardMapper.toCardReadDto(cardWithId1)).thenReturn(cardReadDto1);

        CardReadDto actual = cardServiceImpl.updateCard(ID_1L, cardWriteDto);

        Assertions.assertEquals(cardReadDto1, actual);
        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verify(cardRepository).findCardById(ID_1L);
        Mockito.verify(cardMapper).updateCardFromDto(cardWriteDto, cardWithId1);
        Mockito.verify(cardRepository).save(cardWithId1);
        Mockito.verify(cardMapper).toCardReadDto(cardWithId1);
        Mockito.verifyNoMoreInteractions(userRepository, cardRepository, cardMapper);
    }

    @Test
    void updateCard_shouldThrowException_whenUserNotFound() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> cardServiceImpl.updateCard(ID_1L, cardWriteDto));

        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verifyNoInteractions(cardRepository, cardMapper);
    }

    @Test
    void updateCard_shouldThrowException_whenCardToUpdateNotFound() {
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(true);
        Mockito.when(cardRepository.findCardById(ID_1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> cardServiceImpl.updateCard(ID_1L, cardWriteDto));

        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verify(cardRepository).findCardById(ID_1L);
        Mockito.verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void updateCard_shouldThrowException_whenNewCardNumberIsNotUnique() {
        cardWithId1.setNumber(CARD_NUMBER_2);
        Mockito.when(userRepository.existsById(ID_1L)).thenReturn(true);
        Mockito.when(cardRepository.findCardById(ID_1L)).thenReturn(Optional.of(cardWithId1));
        Mockito.when(cardRepository.existsByNumber(CARD_NUMBER_1)).thenReturn(true);

        Assertions.assertThrows(EntityIsNotUniqueCustomException.class, () -> cardServiceImpl.updateCard(ID_1L, cardWriteDto));

        Mockito.verify(userRepository).existsById(ID_1L);
        Mockito.verify(cardRepository).findCardById(ID_1L);
        Mockito.verify(cardRepository).existsByNumber(CARD_NUMBER_1);
        Mockito.verifyNoMoreInteractions(cardRepository, cardMapper);
    }

    @Test
    void deleteCardById_shouldDeleteCardAndEvictCache_whenCardExists() {
        Mockito.when(cardRepository.findById(ID_1L)).thenReturn(Optional.of(cardWithId1));
        Mockito.when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);

        cardServiceImpl.deleteCardById(ID_1L);

        Mockito.verify(cardRepository).findById(ID_1L);
        Mockito.verify(cacheManager).getCache(CACHE_NAME);
        Mockito.verify(cache).evict(ID_1L);
        Mockito.verify(cardRepository).delete(cardWithId1);
        Mockito.verifyNoMoreInteractions(cardRepository, cacheManager, cache);
        Mockito.verifyNoInteractions(userRepository, cardMapper);
    }

    @Test
    void deleteCardById_shouldThrowException_whenCardNotFound() {
        Mockito.when(cardRepository.findById(ID_1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> cardServiceImpl.deleteCardById(ID_1L));

        Mockito.verify(cardRepository).findById(ID_1L);
        Mockito.verifyNoInteractions(userRepository, cardMapper, cacheManager);
    }
}
