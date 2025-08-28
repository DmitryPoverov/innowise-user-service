package by.innowise.poverov.service;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;
import by.innowise.poverov.entity.User;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceImplIntegrationTest extends BaseIntegrationTest {

    public static final String NAME_1 = "Name_1";
    public static final String SURNAME_1 = "Surname_1";
    public static final String MAIL_1 = "mail1@mail.com";
    public static final LocalDate BIRTH_DATE = LocalDate.of(2001, 1, 1);
    public static final String NAME_2 = "Name_2";
    public static final String SURNAME_2 = "Surname_2";
    public static final String MAIL_2 = "mail2@mail.com";
    public static final String CACHE_NAME = "redis_cache_for_users";
    public static final String NEW_NAME = "New Name";
    public static final String NEW_SURNAME = "New Surname";

    public static User user1;
    public static User user2;
    public static UserWriteDto userWriteDto1;
    public static UserWriteDto userWriteDto2;
    public static UserWriteDto userUpdateDto;
    public static UserWriteDto userWriteDtoWithDuplicateEmail;

    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CacheManager cacheManager;


    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name(NAME_1)
                .surname(SURNAME_1)
                .email(MAIL_1)
                .birthDate(BIRTH_DATE)
                .build();

        user2 = User.builder()
                .name(NAME_2)
                .surname(SURNAME_2)
                .email(MAIL_2)
                .birthDate(BIRTH_DATE)
                .build();

        userWriteDto1 = UserWriteDto.builder()
                .name(NAME_1)
                .surname(SURNAME_1)
                .email(MAIL_1)
                .birthDate(BIRTH_DATE)
                .build();

        userWriteDto2 = UserWriteDto.builder()
                .name(NAME_2)
                .surname(SURNAME_2)
                .email(MAIL_2)
                .birthDate(BIRTH_DATE)
                .build();

        userUpdateDto = UserWriteDto.builder()
                .email(MAIL_1)
                .name(NEW_NAME)
                .surname(NEW_SURNAME)
                .birthDate(BIRTH_DATE)
                .build();

        userWriteDtoWithDuplicateEmail = UserWriteDto.builder()
                .name(NAME_2)
                .surname(SURNAME_2)
                .email(MAIL_1)
                .birthDate(BIRTH_DATE)
                .build();


        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }


    @Test
    void saveUser_shouldSaveUserToDatabase_whenEmailIsUnique() {

        UserReadDto savedUserDto = userService.saveUser(userWriteDto1);

        Assertions.assertThat(savedUserDto.getId()).isNotNull();
        Assertions.assertThat(savedUserDto.getEmail()).isEqualTo(MAIL_1);

        User userInDb = userRepository.findById(savedUserDto.getId()).orElse(null);
        Assertions.assertThat(userInDb).isNotNull();
        Assertions.assertThat(userInDb.getName()).isEqualTo(NAME_1);
    }


    @Test
    void saveUser_shouldThrowException_whenEmailIsNotUnique() {
        userRepository.save(user1);

        assertThrows(EntityIsNotUniqueCustomException.class, () -> userService.saveUser(userWriteDtoWithDuplicateEmail));
    }


    @Test
    void findUserById_shouldReturnUser_whenUserExists() {
        User savedUser = userRepository.save(user1);

        Long userId = savedUser.getId();
        UserReadDto foundUserDto = userService.findUserById(userId);

        Assertions.assertThat(foundUserDto).isNotNull();
        Assertions.assertThat(foundUserDto.getId()).isEqualTo(userId);
        Assertions.assertThat(foundUserDto.getName()).isEqualTo(NAME_1);
    }


    @Test
    void findUserById_shouldThrowException_whenUserDoesNotExist() {
        long nonExistentId = 999L;

        assertThrows(EntityNotFoundCustomException.class, () -> userService.findUserById(nonExistentId));
    }


    @Test
    void updateUserById_shouldUpdateUserData_whenDataIsValid() {
        User userToUpdate = userRepository.save(user1);

        Long updatedUserId = userToUpdate.getId();

        userService.updateUserById(updatedUserId, userWriteDto2);

        User updatedUserInDb = userRepository.findById(updatedUserId).orElseThrow();
        Assertions.assertThat(updatedUserInDb.getName()).isEqualTo(NAME_2);
        Assertions.assertThat(updatedUserInDb.getEmail()).isEqualTo(MAIL_2);
    }


    @Test
    void deleteUserById_shouldRemoveUserFromDatabase() {
        User userToDelete = userRepository.save(user1);

        long userId = userToDelete.getId();
        Assertions.assertThat(userRepository.existsById(userId)).isTrue();

        userService.deleteUserById(userId);

        Assertions.assertThat(userRepository.existsById(userId)).isFalse();
    }


    @Test
    void updateUserById_shouldThrowException_whenEmailIsNotUnique() {
        userRepository.save(user1);
        User userToUpdate = userRepository.save(user2);



        Long id = userToUpdate.getId();
        assertThrows(EntityIsNotUniqueCustomException.class, () -> userService.updateUserById(id, userUpdateDto));
    }


    @Test
    void updateUserById_shouldThrowException_whenUserDoesNotExist() {
        long nonExistentId = 999L;
        UserWriteDto updateDto = UserWriteDto.builder()
                .email("any@email.com")
                .name("anyName")
                .surname("anySurname")
                .birthDate(BIRTH_DATE)
                .build();

        assertThrows(EntityNotFoundCustomException.class, () -> userService.updateUserById(nonExistentId, updateDto));
    }


    @Test
    void deleteUserById_shouldThrowException_whenUserDoesNotExist() {
        long nonExistentId = 999L;

        assertThrows(EntityNotFoundCustomException.class, () -> userService.deleteUserById(nonExistentId));
    }


    @Test
    void findUserById_shouldUseCache_whenCalledMultipleTimes() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();

        UserReadDto userFromFirstCall = userService.findUserById(userId);
        Assertions.assertThat(userFromFirstCall).isNotNull();

        userRepository.deleteById(userId);
        userRepository.flush();

        UserReadDto userFromSecondCall = userService.findUserById(userId);

        Assertions.assertThat(userFromSecondCall).isNotNull();
        Assertions.assertThat(userFromSecondCall.getId()).isEqualTo(userId);
    }

    @Test
    void updateUserById_shouldEvictCache() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();

        userService.findUserById(userId);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        Assertions.assertThat(cache).isNotNull();
        Assertions.assertThat(cache.get(userId)).isNotNull();

        UserWriteDto updateDto = UserWriteDto.builder().name("NewName").email(MAIL_2).build();
        userService.updateUserById(userId, updateDto);

        Assertions.assertThat(cache.get(userId)).isNull();
    }

    @Test
    void deleteUserById_shouldEvictCache() {
        User savedUser = userRepository.save(user1);
        Long userId = savedUser.getId();

        userService.findUserById(userId);

        Cache cache = cacheManager.getCache(CACHE_NAME);
        Assertions.assertThat(cache).isNotNull();
        Assertions.assertThat(cache.get(userId)).isNotNull();

        userService.deleteUserById(userId);

        Assertions.assertThat(cache.get(userId)).isNull();
    }
}
