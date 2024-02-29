package mate.academy.springbookapp.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CreateBookRequestDto(
        @NotBlank(message = "title may not be blank")
        String title,
        @NotBlank(message = "author may not be blank")
        String author,
        @NotBlank(message = "isbn may not be blank")
        String isbn,
        @NotNull
        @PositiveOrZero
        BigDecimal price,
        String description,
        String coverImage) {
}
