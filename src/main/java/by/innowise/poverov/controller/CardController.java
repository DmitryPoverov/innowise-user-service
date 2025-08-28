package by.innowise.poverov.controller;

import by.innowise.poverov.dto.CardReadDto;
import by.innowise.poverov.dto.CardWriteDto;
import by.innowise.poverov.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/innowise/api/v1/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;


    @PostMapping
    public ResponseEntity<CardReadDto> createCard(@RequestBody @Valid CardWriteDto dto) {
        CardReadDto savedCard = cardService.saveCard(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCard);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CardReadDto> getCardById(@PathVariable @Positive Long id) {
        CardReadDto savedCard = cardService.findCardById(id);
        return ResponseEntity.ok(savedCard);
    }


    @GetMapping(params = "ids")
    public ResponseEntity<List<CardReadDto>> getAllCardsByIds(@RequestParam @NotEmpty List<Long> ids) {
        List<CardReadDto> allCardsByIds = cardService.findAllCardsByIds(ids);
        return ResponseEntity.ok(allCardsByIds);
    }


    @PutMapping("/{id}")
    public ResponseEntity<CardReadDto> updateUserById(@RequestBody @Valid CardWriteDto cardWriteDto,
                                                      @PathVariable @Positive Long id) {
        CardReadDto updatedCard = cardService.updateCard(id, cardWriteDto);
        return ResponseEntity.ok(updatedCard);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardById(@PathVariable @Positive Long id) {
        cardService.deleteCardById(id);
        return ResponseEntity.noContent().build();
    }
}
