package by.innowise.poverov.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserWriteDto {

    @NotBlank(message = "[DP_validation_error_message]: The name must be not noll, not empty, not blank.")
    @Size(max = 64)
    private String name;

    @NotBlank
    @Size(max = 64)
    private String surname;

    @NotNull
    @Past
    private LocalDate birthDate;

    @NotBlank
    @Email
    @Size(max = 64)
    private String email;
}
