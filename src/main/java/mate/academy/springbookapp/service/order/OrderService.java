package mate.academy.springbookapp.service.order;

import java.util.List;
import mate.academy.springbookapp.dto.order.OrderDto;
import mate.academy.springbookapp.dto.order.PlaceOrderRequestDto;
import mate.academy.springbookapp.dto.order.UpdateStatusRequestDto;
import mate.academy.springbookapp.dto.orderitem.OrderItemDto;
import mate.academy.springbookapp.model.User;

public interface OrderService {
    List<OrderDto> getAllOrders(Long userId);

    List<OrderItemDto> getAllItemsFromOrder(Long userId, Long orderId);

    OrderItemDto getItemFromOrder(Long userId, Long orderId, Long itemId);

    OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto);

    void updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto);
}
