package by.innowise.poverov.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class CardReadDto implements Serializable {

    private Long id;

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
