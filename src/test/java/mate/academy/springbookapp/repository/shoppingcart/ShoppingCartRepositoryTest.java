package mate.academy.springbookapp.repository.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.model.CartItem;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.model.Role;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ShoppingCartRepositoryTest {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    @DisplayName("Find shopping cart with items by invalid user ID")
    @Sql(scripts = {"classpath:database/shoppingcart/add_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCartWithItemsByUserId_InvalidUserId_ReturnsNull() {
        long invalidUserId = 20L;
        ShoppingCart actual = shoppingCartRepository.findCartWithItemsByUserId(invalidUserId);
        assertNull(actual);
    }

    @Test
    @DisplayName("Find a shopping cart with items by valid user ID")
    @Sql(scripts = {"classpath:database/shoppingcart/add_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findCartWithItemsByUserId_ValidUserId_ReturnsShoppingCart() {
        long validUserId = 1L;
        ShoppingCart expected = getTestShoppingCart(validUserId);
        ShoppingCart actual = shoppingCartRepository.findCartWithItemsByUserId(validUserId);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
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
        ShoppingCart expectedCart = new ShoppingCart();
        expectedCart.setId(1L);
        expectedCart.setUser(user);
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setShoppingCart(expectedCart);
        cartItem.setBook(book);
        cartItem.setQuantity(2);
        HashSet<CartItem> cartItemHashSet = new HashSet<>();
        cartItemHashSet.add(cartItem);
        expectedCart.setCartItems(cartItemHashSet);
        return expectedCart;
    }
}
