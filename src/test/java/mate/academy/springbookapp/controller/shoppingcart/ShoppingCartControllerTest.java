/*package mate.academy.springbookapp.controller.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springbookapp.dto.cartitem.CartItemDto;
import mate.academy.springbookapp.dto.cartitem.UpdateCartItemRequestDto;
import mate.academy.springbookapp.dto.shoppingcart.ShoppingCartDto;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.model.CartItem;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.model.Role;
import mate.academy.springbookapp.model.ShoppingCart;
import mate.academy.springbookapp.model.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static final String USER_EMAIL = "kuba@gmail.com";
    private static final String BASE_ENDPOINT = "/api/cart";
    private static final String DB_PATH_ADD_THREE_CARTS
            = "database/shoppingcart/add_shopping_cart.sql";
    private static final String DB_PATH_DELETE_THREE_CARTS
            = "database/shoppingcart/delete_shopping_cart.sql";

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        executeSqlScript(dataSource, DB_PATH_ADD_THREE_CARTS);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, DB_PATH_DELETE_THREE_CARTS);
    }

    @SneakyThrows
    static void executeSqlScript(DataSource dataSource, String dbPath) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(dbPath));
        }
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart.sql",
            "classpath:database/shoppingcart/add_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteItemFromCart_ValidCartItemId_ReturnsNoContentStatus() throws Exception {
        long validCartItemId = 1L;
        mockMvc.perform(delete(BASE_ENDPOINT + "/cart-item/" + validCartItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @WithUserDetails(USER_EMAIL)
    @Test
    void getCartWithItems_ValidUser_ReturnsShoppingCartDto() throws Exception {
        long validUserId = 1L;
        ShoppingCart modelCart = getTestShoppingCart(validUserId);
        ShoppingCartDto expected = getTestShoppingCartDtoFromModel(modelCart);
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        ShoppingCartDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), ShoppingCartDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/shoppingcart/delete_shopping_cart.sql",
            "classpath:database/shoppingcart/add_shopping_cart.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateItemQuantity_ValidCartItemIdAndRequestDto_ReturnsUpdatedCartItemDto()
            throws Exception {
        long validCartItemId = 1L;
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(10);
        CartItem modelItem = getTestShoppingCart(1L).getCartItems().stream()
                .findFirst()
                .get();
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        CartItemDto expected = new CartItemDto(
                modelItem.getId(),
                modelItem.getBook().getId(),
                modelItem.getBook().getTitle(),
                modelItem.getQuantity() + requestDto.quantity());
        MvcResult result = mockMvc.perform(
                        put(BASE_ENDPOINT + "/cart-item/" + validCartItemId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CartItemDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CartItemDto.class);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
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
*/
