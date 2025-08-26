package by.innowise.poverov.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class UserReadDto implements Serializable {

    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
