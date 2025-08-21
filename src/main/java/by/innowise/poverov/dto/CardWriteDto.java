package by.innowise.poverov.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardWriteDto {

    @NotBlank
    @Pattern(regexp = "\\d{16}", message = "The number has to have exactly 16 figures.")
    private String number;

    @NotBlank
    @Size(max = 64, message = "The holder name has to have less than 64 letters.")
    private String holder;

    @NotNull
    @FutureOrPresent(message = "The expirationDate must be today or in the Future.")
    private LocalDate expirationDate;

    @NotNull
    private Long userId;
}
