package by.innowise.poverov.dto;

import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class CardReadDto {

    private Long id;
    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
