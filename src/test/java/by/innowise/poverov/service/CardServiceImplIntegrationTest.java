package by.innowise.poverov.service;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.entity.Card;
import by.innowise.poverov.entity.User;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.repository.CardRepository;
import by.innowise.poverov.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class CardServiceImplIntegrationTest extends BaseIntegrationTest {

    public static final String CARD_NUMBER_1 = "1111222233334444";
    public static final String CARD_NUMBER_2 = "5555666677778888";
    public static final String CARD_HOLDER_1 = "CARD HOLDER 1";
    public static final String CARD_HOLDER_2 = "CARD HOLDER 2";
    public static final LocalDate EXPIRATION_DATE = LocalDate.now().plusYears(1);
    public static final String CACHE_NAME = "redis_cache_for_users";

    @Autowired
    private CardServiceImpl cardService;
    @Autowired
    private UserService userService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;

    private User savedUser;
    private Card card1;
    private CardWriteDto cardWriteDto1;
    private CardWriteDto cardWriteDto2;
    private CardWriteDto duplicateCardDto;

    @BeforeEach
    void setUp() {
        savedUser = userRepository.save(User.builder()
                .name("Test")
                .surname("User")
                .email("card-owner@test.com")
                .birthDate(LocalDate.now().minusYears(20))
                .build());

        card1 = Card.builder()
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE)
                .user(savedUser)
                .build();

        cardWriteDto1 = CardWriteDto.builder()
                .number(CARD_NUMBER_1)
                .holder(CARD_HOLDER_1)
                .expirationDate(EXPIRATION_DATE)
                .userId(savedUser.getId())
                .build();

        cardWriteDto2 = CardWriteDto.builder()
                .number(CARD_NUMBER_2)
                .holder(CARD_HOLDER_2)
                .expirationDate(EXPIRATION_DATE.plusYears(1))
                .userId(savedUser.getId())
                .build();

        duplicateCardDto = CardWriteDto.builder()
                .number(CARD_NUMBER_1) // Такой же номер, как у card1
                .holder(CARD_HOLDER_2)
                .expirationDate(EXPIRATION_DATE)
                .userId(savedUser.getId())
                .build();

        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }



    @Test
    void saveCard_shouldSaveCard_whenDataIsValid() {
        CardReadDto savedCard = cardService.saveCard(cardWriteDto1);

        assertThat(savedCard.getId()).isNotNull();
        assertThat(savedCard.getNumber()).isEqualTo(CARD_NUMBER_1);
        assertThat(cardRepository.existsById(savedCard.getId())).isTrue();
    }

    @Test
    void saveCard_shouldThrowException_whenCardNumberIsNotUnique() {
        cardRepository.save(card1);

        assertThrows(EntityIsNotUniqueCustomException.class, () -> cardService.saveCard(duplicateCardDto));
    }

    @Test
    void saveCard_shouldEvictUserCache() {
        userService.findUserById(savedUser.getId());
        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(savedUser.getId())).isNotNull();

        cardService.saveCard(cardWriteDto1);

        assertThat(cache.get(savedUser.getId())).isNull();
    }



    @Test
    void findCardById_shouldReturnCard_whenCardExists() {
        Card savedCard = cardRepository.save(card1);

        CardReadDto foundCard = cardService.findCardById(savedCard.getId());

        assertThat(foundCard).isNotNull();
        assertThat(foundCard.getId()).isEqualTo(savedCard.getId());
        assertThat(foundCard.getNumber()).isEqualTo(CARD_NUMBER_1);
    }

    @Test
    void findCardById_shouldThrowException_whenCardDoesNotExist() {
        assertThrows(EntityNotFoundCustomException.class, () -> cardService.findCardById(999L));
    }



    @Test
    void updateCard_shouldUpdateCard_whenDataIsValid() {
        Card cardToUpdate = cardRepository.save(card1);

        cardService.updateCard(cardToUpdate.getId(), cardWriteDto2);

        Card updatedCardInDb = cardRepository.findById(cardToUpdate.getId()).orElseThrow();
        assertThat(updatedCardInDb.getNumber()).isEqualTo(CARD_NUMBER_2);
        assertThat(updatedCardInDb.getHolder()).isEqualTo(CARD_HOLDER_2);
    }

    @Test
    void updateCard_shouldThrowException_whenCardDoesNotExist() {
        assertThrows(EntityNotFoundCustomException.class, () -> cardService.updateCard(999L, cardWriteDto1));
    }

    @Test
    void updateCard_shouldEvictUserCache() {
        Card cardToUpdate = cardRepository.save(card1);
        userService.findUserById(savedUser.getId());

        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(savedUser.getId())).isNotNull();

        cardService.updateCard(cardToUpdate.getId(), cardWriteDto2);

        assertThat(cache.get(savedUser.getId())).isNull();
    }



    @Test
    void deleteCardById_shouldDeleteCard_whenCardExists() {
        Card cardToDelete = cardRepository.save(card1);
        Long cardId = cardToDelete.getId();
        assertThat(cardRepository.existsById(cardId)).isTrue();

        cardService.deleteCardById(cardId);

        assertThat(cardRepository.existsById(cardId)).isFalse();
    }

    @Test
    void deleteCardById_shouldThrowException_whenCardDoesNotExist() {
        assertThrows(EntityNotFoundCustomException.class, () -> cardService.deleteCardById(999L));
    }

    @Test
    void deleteCardById_shouldEvictUserCache() {
        Card cardToDelete = cardRepository.save(card1);
        Long cardId = cardToDelete.getId();
        Long savedUserId = savedUser.getId();
        userService.findUserById(savedUserId);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        assertThat(cache).isNotNull();
        assertThat(cache.get(savedUserId)).isNotNull();

        cardService.deleteCardById(cardId);

        assertThat(cache.get(savedUserId)).isNull();
    }
}
