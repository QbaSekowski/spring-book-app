package mate.academy.springbookapp.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(String message, LocalDateTime localDateTime) {
}
