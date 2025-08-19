package by.innowise.poverov.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundCustomException.class)
    public ResponseEntity<ExceptionDto> handleEntityNotFoundCustomException(EntityNotFoundCustomException exception,
                                                        HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        ExceptionDto exceptionDto = getExceptionDto(status, exception, request);
        return ResponseEntity.status(status).body(exceptionDto);
    }


    @ExceptionHandler(EntityIsNotUniqueCustomException.class)
    public ResponseEntity<ExceptionDto> handleEntityIsNotUniqueCustomException(EntityIsNotUniqueCustomException exception,
                                                        HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        ExceptionDto exceptionDto = getExceptionDto(status, exception, request);
        return ResponseEntity.status(status).body(exceptionDto);
    }


    private static ExceptionDto getExceptionDto(HttpStatus status, Exception exception, HttpServletRequest request) {
        return ExceptionDto.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .errorReason(status.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();
    }
}
