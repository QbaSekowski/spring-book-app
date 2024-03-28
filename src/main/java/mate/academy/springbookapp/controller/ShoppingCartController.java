package mate.academy.springbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.cartitem.CartItemDto;
import mate.academy.springbookapp.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbookapp.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbookapp.service.shoppingcart.ShoppingCartService;
import mate.academy.springbookapp.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Shopping cart management", description = "Endpoints for managing shopping carts")
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @Operation(summary = "Get the shopping cart with all cart items",
            description = "Get the shopping cart with all cart items of the authenticated user")
    public ShoppingCartDto getCartWithItems(Authentication authentication) {
        return shoppingCartService.getCartWithItems(
                userService.getUserFromAuthentication(authentication).getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @Operation(summary = "Add a cart item to the shopping cart",
            description = "Add a cart item to the shopping cart of the authenticated user")
    public ShoppingCartDto addItemToCart(Authentication authentication,
                                             @RequestBody @Valid
                                             CreateCartItemRequestDto createCartItemRequestDto) {
        return shoppingCartService.addItemToCart(
                userService.getUserFromAuthentication(authentication).getId(),
                createCartItemRequestDto);
    }

    @PutMapping("/cart-item/{cartItemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @Operation(summary = "Update cart item's quantity in the shopping cart",
            description = "Update cart item's quantity "
                    + "in the shopping cart of the authenticated user")
    public CartItemDto updateItemQuantity(@RequestBody @Valid
                                              UpdateCartItemRequestDto updateCartItemRequestDto,
                                                    @PathVariable Long cartItemId) {
        return shoppingCartService.updateItemQuantity(cartItemId, updateCartItemRequestDto);
    }

    @DeleteMapping("/cart-item/{cartItemId}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    @Operation(summary = "Delete a cart item in the shopping cart",
            description = "Delete a cart item in the shopping cart of the authenticated user")
    public void deleteItemFromCart(@PathVariable Long cartItemId) {
        shoppingCartService.deleteItemFromCart(cartItemId);
    }
}
