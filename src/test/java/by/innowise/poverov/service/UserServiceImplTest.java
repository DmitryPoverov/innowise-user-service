package by.innowise.poverov.service;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;
import by.innowise.poverov.entity.User;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.mapper.UserMapper;
import by.innowise.poverov.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private static final Long ID_1L = 1L;
    private static final LocalDate BIRTH_DATE1 = LocalDate.of(2001, 1, 1);
    private static final String NAME_1 = "name1";
    private static final String SURNAME_1 = "surname1";
    private static final String EMAIL_1 = "email1@mail.com";

    private static final LocalDate BIRTH_DATE1_NEW = LocalDate.of(2011, 11, 11);
    private static final String NAME_1_NEW = "name1-new";
    private static final String SURNAME_1_NEW = "surname1-new";
    private static final String EMAIL_1_NEW = "email1-new@mail.com";

    private static final Long ID_2L = 2L;
    private static final LocalDate BIRTH_DATE_2 = LocalDate.of(2002, 2, 2);
    private static final String NAME_2 = "name2";
    private static final String SURNAME_2 = "surname2";
    private static final String EMAIL_2 = "email2@mail.com";
    public static final List<User> EMPTY_USER_LIST = Collections.emptyList();
    public static final List<UserReadDto> EMPTY_USER_READ_DTO_LIST = Collections.emptyList();

    private List<Long> idsList;
    private User userWithoutId;
    private User userWithId1;
    private User userWithId1New;
    private User userWithId2;
    private List<User> userList;
    private List<UserReadDto> userReadDtoList;
    private UserReadDto userReadDto1;
    private UserReadDto userReadDto2;
    private UserWriteDto userWriteDto1;
    private UserWriteDto userWriteDto1New;
    private UserReadDto userReadDto1New;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @BeforeEach
    void setUp() {
        userWithoutId = User.builder()
                .name(NAME_1)
                .surname(SURNAME_1)
                .birthDate(BIRTH_DATE1)
                .email(EMAIL_1)
                .build();

        userWithId1 = User.builder()
                .id(ID_1L)
                .name(NAME_1)
                .surname(SURNAME_1)
                .birthDate(BIRTH_DATE1)
                .email(EMAIL_1)
                .build();

        userWithId1New = User.builder()
                .id(ID_1L)
                .name(NAME_1_NEW)
                .surname(SURNAME_1_NEW)
                .birthDate(BIRTH_DATE1_NEW)
                .email(EMAIL_1_NEW)
                .build();

        userWithId2 = User.builder()
                .id(ID_2L)
                .name(NAME_2)
                .surname(SURNAME_2)
                .birthDate(BIRTH_DATE_2)
                .email(EMAIL_2)
                .build();

        userWriteDto1 = UserWriteDto.builder()
                .name(NAME_1)
                .surname(SURNAME_1)
                .birthDate(BIRTH_DATE1)
                .email(EMAIL_1)
                .build();

        userWriteDto1New = UserWriteDto.builder()
                .name(NAME_1_NEW)
                .surname(SURNAME_1_NEW)
                .birthDate(BIRTH_DATE1_NEW)
                .email(EMAIL_1_NEW)
                .build();

        userReadDto1New = UserReadDto.builder()
                .name(NAME_1_NEW)
                .surname(SURNAME_1_NEW)
                .birthDate(BIRTH_DATE1_NEW)
                .email(EMAIL_1_NEW)
                .build();

        userReadDto1 = UserReadDto.builder()
                .id(ID_1L)
                .name(NAME_1)
                .surname(SURNAME_1)
                .birthDate(BIRTH_DATE1)
                .email(EMAIL_1)
                .build();

        userReadDto2 = UserReadDto.builder()
                .id(ID_2L)
                .name(NAME_2)
                .surname(SURNAME_2)
                .birthDate(BIRTH_DATE_2)
                .email(EMAIL_2)
                .build();

        idsList = List.of(ID_1L, ID_2L);
        userList = List.of(userWithId1, userWithId2);
        userReadDtoList = List.of(userReadDto1, userReadDto2);
    }


    @Test
    void saveUser_ShouldReturnUserReadDto_WhenValidUserWriteDtoProvided() {
        Mockito.when(userRepository.existsByEmail(userWriteDto1.getEmail())).thenReturn(false);
        Mockito.when(userMapper.toUser(userWriteDto1)).thenReturn(userWithoutId);
        Mockito.when(userRepository.save(userWithoutId)).thenReturn(userWithId1);
        Mockito.when(userMapper.toUserReadDto(userWithId1)).thenReturn(userReadDto1);

        UserReadDto actual = userServiceImpl.saveUser(userWriteDto1);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userReadDto1, actual);

        Mockito.verify(userRepository).existsByEmail(userWriteDto1.getEmail());
        Mockito.verify(userMapper).toUser(userWriteDto1);
        Mockito.verify(userRepository).save(userWithoutId);
        Mockito.verify(userMapper).toUserReadDto(userWithId1);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void saveUser_ShouldThrowException_WhenInvalidUserWriteDtoProvided() {
        Mockito.when(userRepository.existsByEmail(userWriteDto1.getEmail())).thenReturn(true);

        Assertions.assertThrows(EntityIsNotUniqueCustomException.class, () -> userServiceImpl.saveUser(userWriteDto1));

        Mockito.verify(userRepository).existsByEmail(userWriteDto1.getEmail());
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void findUserById_ShouldReturnUserReadDto_WhenValidUserReadDtoProvided() {
        Mockito.when(userRepository.findById(ID_1L)).thenReturn(Optional.of(userWithId1));
        Mockito.when(userMapper.toUserReadDto(userWithId1)).thenReturn(userReadDto1);

        UserReadDto actual = userServiceImpl.findUserById(ID_1L);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userReadDto1, actual);
        Mockito.verify(userRepository).findById(ID_1L);
        Mockito.verify(userMapper).toUserReadDto(userWithId1);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void findUserById_ShouldThrowException_WhenInvalidUserReadDtoProvided() {
        Mockito.when(userRepository.findById(ID_1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> userServiceImpl.findUserById(ID_1L));

        Mockito.verify(userRepository, Mockito.times(1)).findById(ID_1L);
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void findAllUsersById_ShouldReturnUserReadDtoList_WhenValidUserIdsProvided() {
        Mockito.when(userRepository.findAllById(idsList)).thenReturn(userList);
        Mockito.when(userMapper.toUserReadDto(userWithId1)).thenReturn(userReadDto1);
        Mockito.when(userMapper.toUserReadDto(userWithId2)).thenReturn(userReadDto2);

        List<UserReadDto> actual = userServiceImpl.findAllUsersById(idsList);

        Assertions.assertEquals(userReadDtoList, actual);
        Mockito.verify(userRepository).findAllById(idsList);
        Mockito.verify(userMapper).toUserReadDto(userWithId1);
        Mockito.verify(userMapper).toUserReadDto(userWithId2);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void findAllUsersById_ShouldReturnEmptyList_WhenInvalidUserIdsProvided() {
        Mockito.when(userRepository.findAllById(idsList)).thenReturn(EMPTY_USER_LIST);

        List<UserReadDto> actual = userServiceImpl.findAllUsersById(idsList);

        Assertions.assertEquals(EMPTY_USER_READ_DTO_LIST, actual);
        Mockito.verify(userRepository).findAllById(idsList);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void findUserByEmail_TestShouldReturnUserReadDto_WhenValidEmailProvided() {
        Mockito.when(userRepository.findUserByEmail(EMAIL_1)).thenReturn(Optional.of(userWithId1));
        Mockito.when(userMapper.toUserReadDto(userWithId1)).thenReturn(userReadDto1);

        UserReadDto actual = userServiceImpl.findUserByEmail(EMAIL_1);

        Assertions.assertEquals(userReadDto1, actual);
        Mockito.verify(userRepository).findUserByEmail(EMAIL_1);
        Mockito.verify(userMapper).toUserReadDto(userWithId1);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void findUserByEmail_TestShouldThrows_WhenInvalidEmailProvided() {
        Mockito.when(userRepository.findUserByEmail(EMAIL_1)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> userServiceImpl.findUserByEmail(EMAIL_1));
        Mockito.verify(userRepository).findUserByEmail(EMAIL_1);
        Mockito.verifyNoInteractions(userMapper);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void updateUserById_TestShouldUpdateUserReadDto_WhenValidUserReadDtoProvided() {
        Mockito.when(userRepository.findById(ID_1L)).thenReturn(Optional.of(userWithId1));
        Mockito.when(userRepository.existsByEmail(EMAIL_1_NEW)).thenReturn(false);
        Mockito.when(userRepository.save(userWithId1)).thenReturn(userWithId1New);
        Mockito.when(userMapper.toUserReadDto(userWithId1New)).thenReturn(userReadDto1New);

        UserReadDto actual = userServiceImpl.updateUserById(ID_1L, userWriteDto1New);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(userReadDto1New, actual);
        Mockito.verify(userRepository).findById(ID_1L);
        Mockito.verify(userRepository).existsByEmail(EMAIL_1_NEW);
        Mockito.verify(userMapper).updateUserFromDto(userWriteDto1New, userWithId1);
        Mockito.verify(userRepository).save(userWithId1);
        Mockito.verify(userMapper).toUserReadDto(userWithId1New);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void updateUserById_TestShouldThrows_WhenInvalidUserIdProvided() {
        Mockito.when(userRepository.findById(ID_1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> userServiceImpl.updateUserById(ID_1L, userWriteDto1));

        Mockito.verify(userRepository).findById(ID_1L);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }


    @Test
    void updateUserById_TestShouldThrows_WhenInvalidEmailProvided() {
        Mockito.when(userRepository.findById(ID_1L)).thenReturn(Optional.of(userWithId1));
        Mockito.when(userRepository.existsByEmail(EMAIL_1_NEW)).thenReturn(true);

        Assertions.assertThrows(EntityIsNotUniqueCustomException.class, () -> userServiceImpl.updateUserById(ID_1L, userWriteDto1New));

        Mockito.verify(userRepository).findById(ID_1L);
        Mockito.verify(userRepository).existsByEmail(EMAIL_1_NEW);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void deleteUserById_TestShouldReturnNone_WhenValidUserIdProvided() {
        Mockito.when(userRepository.deleteUserById(ID_1L)).thenReturn(1);

        userServiceImpl.deleteUserById(ID_1L);

        Mockito.verify(userRepository).deleteUserById(ID_1L);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void deleteUserById_TestShouldThrows_WhenInvalidUserIdProvided() {
        Mockito.when(userRepository.deleteUserById(ID_1L)).thenReturn(0);

        Assertions.assertThrows(EntityNotFoundCustomException.class, () -> userServiceImpl.deleteUserById(ID_1L));

        Mockito.verify(userRepository).deleteUserById(ID_1L);
        Mockito.verifyNoMoreInteractions(userRepository, userMapper);
    }
}
