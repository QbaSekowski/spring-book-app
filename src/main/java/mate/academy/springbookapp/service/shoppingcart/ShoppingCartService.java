package mate.academy.springbookapp.service.shoppingcart;

import mate.academy.springbookapp.dto.cartitem.CartItemDto;
import mate.academy.springbookapp.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbookapp.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;

public interface ShoppingCartService {
    ShoppingCartDto getCartWithItems(Long userId);

    ShoppingCartDto addItemToCart(Long userId,
                                      CreateCartItemRequestDto createCartItemRequestDto);

    CartItemDto updateItemQuantity(Long cartItemId,
                                   UpdateCartItemRequestDto updateCartItemRequestDto);

    void deleteItemFromCart(Long cartItemId);
}
