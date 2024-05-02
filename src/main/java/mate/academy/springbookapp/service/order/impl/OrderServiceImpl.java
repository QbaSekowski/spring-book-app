package mate.academy.springbookapp.service.order.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.order.OrderDto;
import mate.academy.springbookapp.dto.order.PlaceOrderRequestDto;
import mate.academy.springbookapp.dto.order.UpdateStatusRequestDto;
import mate.academy.springbookapp.dto.orderitem.OrderItemDto;
import mate.academy.springbookapp.exception.AccessDeniedException;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.exception.InvalidStatusException;
import mate.academy.springbookapp.mapper.OrderItemMapper;
import mate.academy.springbookapp.mapper.OrderMapper;
import mate.academy.springbookapp.model.Order;
import mate.academy.springbookapp.model.OrderItem;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.model.User;
import mate.academy.springbookapp.repository.cartitem.CartItemRepository;
import mate.academy.springbookapp.repository.order.OrderRepository;
import mate.academy.springbookapp.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbookapp.service.order.OrderService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderDto> getAllOrders(Long userId) {
        return orderRepository.findAllByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public List<OrderItemDto> getAllItemsFromOrder(Long userId, Long orderId) {
        Order modelOrder = createModelOrder(orderId, userId);
        return modelOrder.getOrderItems().stream()
                .map(orderItemMapper::toDto)
                .toList();
    }

    @Override
    public OrderItemDto getItemFromOrder(Long userId, Long orderId, Long itemId) {
        Order modelOrder = createModelOrder(orderId, userId);
        OrderItem modelItem = modelOrder.getOrderItems().stream()
                .filter(oi -> oi.getId().equals(itemId))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find item with id: " + itemId));
        return orderItemMapper.toDto(modelItem);
    }

    @Override
    @Transactional
    public OrderDto placeOrder(User user, PlaceOrderRequestDto requestDto) {
        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(requestDto.shippingAddress());
        ShoppingCart modelCart = shoppingCartRepository.findCartWithItemsByUserId(user.getId());
        Set<OrderItem> orderItems = modelCart.getCartItems().stream()
                .map(orderItemMapper::toOrderItemFromCartItem)
                .collect(Collectors.toSet());
        orderItems.forEach(oi -> oi.setOrder(order));
        order.setOrderItems(orderItems);
        BigDecimal total = modelCart.getCartItems().stream()
                .map(ci -> ci.getBook().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotal(total);
        cartItemRepository.deleteAllByCartId(modelCart.getId());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public void updateOrderStatus(Long orderId, UpdateStatusRequestDto requestDto) {
        Order modelOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        if (!EnumUtils.isValidEnum(Order.Status.class, requestDto.status())) {
            throw new InvalidStatusException("Invalid order status: " + requestDto.status());
        }
        modelOrder.setStatus(Order.Status.valueOf(requestDto.status()));
        orderRepository.save(modelOrder);
    }

    private Order createModelOrder(Long orderId, Long userId) {
        Order modelOrder = orderRepository.findByIdWithItems(orderId).orElseThrow(
                () -> new EntityNotFoundException("Can't find order with id: " + orderId));
        if (!modelOrder.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to access this order");
        }
        return modelOrder;
    }
}
