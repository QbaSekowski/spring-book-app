package mate.academy.springbookapp.dto.shoppingcart;

import java.util.Set;
import mate.academy.springbookapp.dto.cartitem.CartItemDto;

public record ShoppingCartDto(
        Long id,
        Long userId,
        Set<CartItemDto> cartItems) {
}
