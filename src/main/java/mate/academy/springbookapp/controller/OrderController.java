package mate.academy.springbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.order.OrderDto;
import mate.academy.springbookapp.dto.order.PlaceOrderRequestDto;
import mate.academy.springbookapp.dto.order.UpdateStatusRequestDto;
import mate.academy.springbookapp.dto.orderitem.OrderItemDto;
import mate.academy.springbookapp.service.order.OrderService;
import mate.academy.springbookapp.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order management", description = "Endpoints for managing orders")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get order's history",
            description = "Get the list of all orders with details for a specific user")
    public List<OrderDto> getOrdersHistory(Authentication authentication) {
        return orderService.getAllOrders(
                userService.getUserFromAuthentication(authentication).getId());
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get contents of the order",
            description = "Get the list of all items in the order for a specific user")
    public List<OrderItemDto> getAllItemsFromOrder(Authentication authentication,
                                                   @PathVariable Long orderId) {
        return orderService.getAllItemsFromOrder(
                userService.getUserFromAuthentication(authentication).getId(), orderId);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get the item from the order",
            description = "Get the one exact item from the order "
            + "providing item's ID and order's ID")
    public OrderItemDto getItemFromOrder(Authentication authentication, @PathVariable Long orderId,
                                         @PathVariable Long itemId) {
        return orderService.getItemFromOrder(
                userService.getUserFromAuthentication(authentication).getId(), orderId, itemId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Place a new order",
            description = "Place a new order as a specific user providing a shipping address")
    public OrderDto placeOrder(Authentication authentication,
                               @RequestBody @Valid PlaceOrderRequestDto placeOrderRequestDto) {
        return orderService.placeOrder(
                userService.getUserFromAuthentication(authentication), placeOrderRequestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update the status of the order",
            description = "Update the status of the order")
    public void updateOrderStatus(@PathVariable Long id,
                                  @RequestBody @Valid
                                  UpdateStatusRequestDto updateStatusRequestDto) {
        orderService.updateOrderStatus(id, updateStatusRequestDto);
    }
}
