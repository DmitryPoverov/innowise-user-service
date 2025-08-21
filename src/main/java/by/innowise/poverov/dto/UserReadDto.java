package by.innowise.poverov.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class UserReadDto {

    private Long id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
