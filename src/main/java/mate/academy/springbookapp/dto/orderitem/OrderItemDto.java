package mate.academy.springbookapp.dto.orderitem;

public record OrderItemDto(
        Long id,
        Long bookId,
        int quantity) {
}
