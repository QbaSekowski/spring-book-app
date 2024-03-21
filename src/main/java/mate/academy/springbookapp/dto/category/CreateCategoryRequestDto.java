package mate.academy.springbookapp.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequestDto(
        @NotBlank(message = "name may not be blank")
        String name,
        String description) {
}
