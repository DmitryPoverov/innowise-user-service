package by.innowise.poverov.service;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;

import java.util.List;

public interface UserService {

    UserReadDto saveUser(UserWriteDto dto);
    UserReadDto findUserById(Long id);
    List<UserReadDto> findAllByIdIn(List<Long> ids);
    UserReadDto findUserByEmail(String email);
    UserReadDto updateUserById(Long id, UserWriteDto dto);
    int deleteUserById(Long id);
}
