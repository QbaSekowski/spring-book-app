package mate.academy.springbookapp.service.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import mate.academy.springbookapp.dto.cartitem.CartItemDto;
import mate.academy.springbookapp.dto.cartitem.CreateCartItemRequestDto;
import mate.academy.springbookapp.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.CartItemMapper;
import mate.academy.springbookapp.mapper.ShoppingCartMapper;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.model.CartItem;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.model.Role;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.model.User;
import mate.academy.springbookapp.repository.cartitem.CartItemRepository;
import mate.academy.springbookapp.repository.shoppingcart.ShoppingCartRepository;
import mate.academy.springbookapp.service.shoppingcart.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceTest {
    @Mock
    private ShoppingCartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartMapper cartMapper;
    @Mock
    private CartItemMapper cartItemMapper;
    @InjectMocks
    private ShoppingCartServiceImpl cartService;

    @Test
    @DisplayName("Successfully update quantity of a cart item")
    void updateCartItemQuantity_CorrectCartItemIdAndRequestDto_ReturnsUpdatedCartItemDto() {
        Long correctCartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(10);
        CartItem testCartItem = getTestShoppingCart(1L).getCartItems().stream()
                .findFirst()
                .get();
        CartItemDto expectedCartItem = new CartItemDto(
                testCartItem.getId(),
                testCartItem.getBook().getId(),
                testCartItem.getBook().getTitle(),
                testCartItem.getQuantity() + requestDto.quantity());
        when(cartItemRepository.findById(correctCartItemId)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(testCartItem)).thenReturn(testCartItem);
        when(cartItemMapper.toDto(testCartItem)).thenReturn(expectedCartItem);
        CartItemDto actualCartItem = cartService.updateItemQuantity(correctCartItemId, requestDto);
        assertEquals(expectedCartItem, actualCartItem);
        verify(cartItemRepository, times(1)).findById(any());
        verify(cartItemRepository, times(1)).save(any());
        verify(cartItemMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartItemRepository, cartMapper);
    }

    @Test
    @DisplayName("Unsuccessfully update quantity of a cart item "
            + "by providing incorrect cart item ID and throw EntityNotFoundException")
    void updateCartItemQuantity_IncorrectCartItemId_ThrowsEntityNotFoundException() {
        Long incorrectCartItemId = 40L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(10);
        String expectedInfo = "Can't find cart item with id: " + incorrectCartItemId;
        when(cartItemRepository.findById(incorrectCartItemId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> cartService.updateItemQuantity(incorrectCartItemId, requestDto));
        String actualInfo = exception.getMessage();
        assertEquals(expectedInfo, actualInfo);
        verify(cartItemRepository, times(1)).findById(any());
        verifyNoMoreInteractions(cartItemRepository, cartItemMapper);
    }

    @Test
    @DisplayName("Add a cart item to the shopping cart using correct user ID")
    void addCartItemToCart_CorrectUserId_ReturnsUpdatedShoppingCartDto() {
        Long correctUserId = 1L;
        Long nonExistingCartItemBookId = 40L;
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto(
                nonExistingCartItemBookId, 2);
        ShoppingCart testCart = getTestShoppingCart(correctUserId);
        CartItem testItem = getTestCartItemFromRequest(testCart, requestDto);
        ShoppingCartDto expectedCart = getTestShoppingCartDtoFromModel(testCart);
        expectedCart.cartItems().add(
                new CartItemDto(
                        testItem.getId(),
                        testItem.getBook().getId(),
                        testItem.getBook().getTitle(),
                        requestDto.quantity()));
        when(cartRepository.findCartWithItemsByUserId(correctUserId)).thenReturn(testCart);
        when(cartItemMapper.toModel(requestDto)).thenReturn(testItem);
        when(cartRepository.save(testCart)).thenReturn(testCart);
        when(cartMapper.toDto(testCart)).thenReturn(expectedCart);
        ShoppingCartDto actualCart = cartService.addItemToCart(correctUserId, requestDto);
        assertEquals(expectedCart, actualCart);
        verify(cartRepository, times(1)).findCartWithItemsByUserId(any());
        verify(cartItemMapper, times(1)).toModel(any());
        verify(cartRepository, times(1)).save(any());
        verify(cartMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartRepository, cartItemMapper, cartMapper, cartItemRepository);
    }

    @Test
    @DisplayName("Find a shopping cart with items providing a correct user ID")
    void getCartWithItems_CorrectUserId_ReturnsShoppingCartDto() {
        Long correctUserId = 1L;
        ShoppingCart testCart = getTestShoppingCart(correctUserId);
        ShoppingCartDto expectedCart = getTestShoppingCartDtoFromModel(testCart);
        when(cartRepository.findCartWithItemsByUserId(correctUserId)).thenReturn(testCart);
        when(cartMapper.toDto(testCart)).thenReturn(expectedCart);
        ShoppingCartDto actualCart = cartService.getCartWithItems(correctUserId);
        assertEquals(expectedCart, actualCart);
        verify(cartRepository, times(1)).findCartWithItemsByUserId(any());
        verify(cartMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(cartRepository, cartMapper);
    }

    private ShoppingCart getTestShoppingCart(long userId) {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.USER);
        User user = new User();
        user.setId(userId);
        user.setEmail("kuba@gmail.com");
        user.setPassword("haslokuby123");
        user.setFirstName("Jakub");
        user.setLastName("Nowak");
        user.setShippingAddress("Pilsudskiego 4, 31-489 Krakow, Polska");
        user.setRoles(Set.of(role));
        Category category = new Category();
        category.setId(1L);
        category.setName("Comedy");
        category.setDescription("Comedy category");
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Comedy Book");
        book.setAuthor("Author");
        book.setIsbn("2244-5656677-6787");
        book.setPrice(new BigDecimal("35.99"));
        book.setDescription("Book description");
        book.setCoverImage("images.com/image.jpg");
        book.setCategories(Set.of(category));
        ShoppingCart expected = new ShoppingCart();
        expected.setId(1L);
        expected.setUser(user);
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(expected);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        HashSet<CartItem> cartItemHashSet = new HashSet<>();
        cartItemHashSet.add(cartItem);
        expected.setCartItems(cartItemHashSet);
        return expected;
    }

    private ShoppingCartDto getTestShoppingCartDtoFromModel(ShoppingCart shoppingCart) {
        Set<CartItem> cartItems = shoppingCart.getCartItems();
        Set<CartItemDto> cartItemDtoSet = new HashSet<>();
        for (CartItem item : cartItems) {
            cartItemDtoSet.add(
                    new CartItemDto(
                            item.getId(),
                            item.getBook().getId(),
                            item.getBook().getTitle(),
                            item.getQuantity()));
        }
        return new ShoppingCartDto(
                shoppingCart.getId(),
                shoppingCart.getUser().getId(),
                cartItemDtoSet);
    }

    private CartItem getTestCartItemFromRequest(
            ShoppingCart shoppingCart, CreateCartItemRequestDto requestDto) {
        CartItem cartItem = new CartItem();
        cartItem.setId(2L);
        cartItem.setShoppingCart(shoppingCart);
        Book book = new Book(requestDto.bookId());
        book.setTitle("new title");
        book.setAuthor("new author");
        book.setIsbn("2434-6767687-76767");
        book.setPrice(new BigDecimal("50.99"));
        cartItem.setBook(book);
        cartItem.setQuantity(requestDto.quantity());
        return cartItem;
    }
}
