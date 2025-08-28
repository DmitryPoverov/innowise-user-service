package by.innowise.poverov.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                                   HttpServletRequest request) {
        Map<String, String> errors = getAllValidationErrorsFromException(exception);

        HttpStatus status = HttpStatus.BAD_REQUEST;
        ExceptionDto exceptionDto = getExceptionDto(status, request, errors);
        return ResponseEntity.status(status).body(exceptionDto);
    }


    private static Map<String, String> getAllValidationErrorsFromException(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNullElse(fieldError.getDefaultMessage(), "")));
    }


    @ExceptionHandler(EntityNotFoundCustomException.class)
    public ResponseEntity<ExceptionDto> handleEntityNotFoundCustomException(EntityNotFoundCustomException exception,
                                                                            HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        Map<String, String> error = Map.of("error", exception.getMessage());
        ExceptionDto exceptionDto = getExceptionDto(status, request, error);
        return ResponseEntity.status(status).body(exceptionDto);
    }


    @ExceptionHandler(EntityIsNotUniqueCustomException.class)
    public ResponseEntity<ExceptionDto> handleEntityIsNotUniqueCustomException(EntityIsNotUniqueCustomException exception,
                                                                               HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        Map<String, String> error = Map.of("error", exception.getMessage());
        ExceptionDto exceptionDto = getExceptionDto(status, request, error);
        return ResponseEntity.status(status).body(exceptionDto);
    }


    private static ExceptionDto getExceptionDto(HttpStatus status,
                                                HttpServletRequest request,
                                                Map<String, String> errors) {
        return ExceptionDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errors(errors)
                .errorReason(status.getReasonPhrase())
                .path(request.getRequestURI())
                .build();
    }
}
