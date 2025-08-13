package by.innowise.poverov.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(max = 16)
    private String number;

    @NotBlank
    @Size(max = 64)
    private String holder;

    @NotNull
    @FutureOrPresent
    private LocalDate expirationDate;
}
