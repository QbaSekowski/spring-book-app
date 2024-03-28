package mate.academy.springbookapp.service.shoppingcart.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.cartitem.CartItemDto;
import mate.academy.springbookapp.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbookapp.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.CartItemMapper;
import mate.academy.springbookapp.mapper.ShoppingCartMapper;
import mate.academy.springbookapp.model.CartItem;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.repository.cartitem.CartItemRepository;
import mate.academy.springbookapp.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbookapp.service.shoppingcart.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public ShoppingCartDto getCartWithItems(Long userId) {
        return shoppingCartMapper.toDto(shoppingCartRepository.findCartWithItemsByUserId(userId));
    }

    @Override
    public ShoppingCartDto addItemToCart(Long userId,
                                             CreateCartItemRequestDto createCartItemRequestDto) {
        CartItem modelCartItem = cartItemMapper.toModel(createCartItemRequestDto);
        ShoppingCart modelCart = shoppingCartRepository.findCartWithItemsByUserId(userId);
        modelCartItem.setShoppingCart(modelCart);
        modelCart.getCartItems().add(modelCartItem);
        return shoppingCartMapper.toDto(shoppingCartRepository.save(modelCart));
    }

    @Override
    public CartItemDto updateItemQuantity(Long cartItemId,
                                          UpdateCartItemRequestDto updateCartItemRequestDto) {
        CartItem modelCartItem = cartItemRepository.findById(cartItemId).orElseThrow(
                () -> new EntityNotFoundException("Can't find cart item with id: " + cartItemId));
        modelCartItem.setQuantity(updateCartItemRequestDto.quantity());
        return cartItemMapper.toDto(cartItemRepository.save(modelCartItem));
    }

    @Override
    public void deleteItemFromCart(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
