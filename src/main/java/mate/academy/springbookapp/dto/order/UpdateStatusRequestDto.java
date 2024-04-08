package mate.academy.springbookapp.dto.order;

import jakarta.validation.constraints.NotBlank;
import mate.academy.springbookapp.model.Order;

public record UpdateStatusRequestDto(
        @NotBlank
        Order.Status status) {
}
