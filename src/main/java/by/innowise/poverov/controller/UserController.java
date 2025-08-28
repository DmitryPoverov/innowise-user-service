package by.innowise.poverov.controller;

import by.innowise.poverov.dto.UserReadDto;
import by.innowise.poverov.dto.UserWriteDto;
import by.innowise.poverov.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/innowise/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserReadDto> createUser(@RequestBody @Validated UserWriteDto userWriteDto) {
        UserReadDto createdUser = userService.saveUser(userWriteDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserReadDto> findUserById(@PathVariable @Positive Long id) {
        UserReadDto userById = userService.findUserById(id);
        return ResponseEntity.ok(userById);
    }


// check: GET /innowise/api/v1/users?ids=1,2,3
    @GetMapping(params = "ids")
    public ResponseEntity<List<UserReadDto>> findAllUsersByIds(@RequestParam @NotEmpty List<@Positive Long> ids) {
        List<UserReadDto> allUsersById = userService.findAllUsersById(ids);
        return ResponseEntity.ok(allUsersById);
    }


    @GetMapping(params = "email")
    public ResponseEntity<UserReadDto> findAllUsersByEmail(@RequestParam @Email String email) {
        UserReadDto userByEmail = userService.findUserByEmail(email);
        return ResponseEntity.ok(userByEmail);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserReadDto> updateUserById(@PathVariable @Positive Long id,
                                                      @RequestBody @Validated UserWriteDto userWriteDto) {
        UserReadDto updatedUser = userService.updateUserById(id, userWriteDto);
        return ResponseEntity.ok(updatedUser);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable @Positive Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
