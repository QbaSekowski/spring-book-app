package mate.academy.springbookapp.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import mate.academy.springbookapp.dto.orderitem.OrderItemDto;

public record OrderDto(
        Long id,
        Long userId,
        String status,
        BigDecimal total,
        LocalDateTime orderDate,
        Set<OrderItemDto> orderItems) {
}
