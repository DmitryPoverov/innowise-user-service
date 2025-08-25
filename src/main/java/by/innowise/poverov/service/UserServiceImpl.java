package by.innowise.poverov.service;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;
import by.innowise.poverov.entity.User;
import by.innowise.poverov.exception.EntityIsNotUniqueCustomException;
import by.innowise.poverov.exception.EntityNotFoundCustomException;
import by.innowise.poverov.mapper.UserMapper;
import by.innowise.poverov.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserReadDto saveUser(UserWriteDto writeDto) {
        String emailFromDto = writeDto.getEmail();
        if (userRepository.existsByEmail(emailFromDto)) {
            throw new EntityIsNotUniqueCustomException(emailFromDto);
        }

        User user = userMapper.toUser(writeDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserReadDto(savedUser);
    }


    @Override
    @Cacheable(value = "redis_cache_for_users", key = "#id")
    public UserReadDto findUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserReadDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(id));
    }


    @Override
    public List<UserReadDto> findAllUsersById(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toUserReadDto)
                .toList();
    }


    @Override
    @Cacheable(value = "redis_cache_for_users", key = "#email")
    public UserReadDto findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .map(userMapper::toUserReadDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(email));
    }


    @Override
    @Transactional
    @CacheEvict(value = "redis_cache_for_users", key = "#id")
    public UserReadDto updateUserById(Long id, UserWriteDto userWriteDto) {
        User userFromDB = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundCustomException(id));

        String newEmail = userWriteDto.getEmail();

        if (!newEmail.equals(userFromDB.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new EntityIsNotUniqueCustomException(newEmail);
        }
        userMapper.updateUserFromDto(userWriteDto, userFromDB);
        userRepository.save(userFromDB);
        return userMapper.toUserReadDto(userFromDB);
    }


    @Override
    @Transactional
    @CacheEvict(value = "redis_cache_for_users", key = "#id")
    public void deleteUserById(Long id) {
        if (userRepository.deleteUserById(id) == 0) {
            throw new EntityNotFoundCustomException(id);
        }
    }
}
