package mate.academy.springbookapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotBlank(message = "Title may not be blank")
    private String title;
    @NotBlank(message = "Author may not be blank")
    private String author;
    @NotBlank(message = "ISBN may not be blank")
    private String isbn;
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    private String description;
    private String coverImage;
}
