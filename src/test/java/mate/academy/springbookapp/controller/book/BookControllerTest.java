/*package mate.academy.springbookapp.controller.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;
    private static final String CONTROLLER_ENDPOINT = "/api/books";
    private static final String DB_PATH_ADD_THREE_BOOKS_WITH_CATEGORIES
            = "database/book/add-three-books-with-categories.sql";
    private static final String DB_PATH_REMOVE_ALL_BOOKS_WITH_CATEGORIES
            = "database/book/remove-all-books-with-categories.sql";

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
        executeSqlScript(dataSource, DB_PATH_ADD_THREE_BOOKS_WITH_CATEGORIES);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, DB_PATH_REMOVE_ALL_BOOKS_WITH_CATEGORIES);
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
    @Sql(scripts = {"classpath:database/book/delete-created-book.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBook_ValidRequestDto_ReturnsNewBook() throws Exception {
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto();
        BookDto expected = getBookDtoFromRequest(4L, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(post(CONTROLLER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getAll_ThreeBooksInDb_ReturnsAllBooks() throws Exception {
        List<BookDtoWithoutCategoryIds> expected = getThreeBookDtoWithoutCategoryIds();
        MvcResult result = mockMvc.perform(
                        get(CONTROLLER_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/book/remove-all-books-with-categories.sql",
            "classpath:database/book/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBookById_ValidRequestDto_ReturnsUpdatedBook() throws Exception {
        Long idPassed = 2L;
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto();
        BookDto expected = getBookDtoFromRequest(idPassed, requestDto);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(put(CONTROLLER_ENDPOINT + "/" + idPassed)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual.getId());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getBookById_ValidId_ReturnsBookDtoWithoutCategoryIds() throws Exception {
        long validId = 2L;
        BookDtoWithoutCategoryIds expected = getThreeBookDtoWithoutCategoryIds().get(1);

        MvcResult result = mockMvc.perform(
                        get(CONTROLLER_ENDPOINT + "/"
                                + validId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void search_ValidSearchParameters_ReturnsAllMatchingBookDtoWithoutCategoryIds()
            throws Exception {
        List<BookDtoWithoutCategoryIds> expected = getTwoMatchingBookDtoWithoutCategoryIds();
        BookSearchParametersDto searchParameters = new BookSearchParametersDto(
                null, null, null, "35", "50");
        MvcResult result = mockMvc.perform(
                        get(CONTROLLER_ENDPOINT
                                + "/search?minPrice="
                                + searchParameters.minPrice()
                                + "&maxPrice="
                                + searchParameters.maxPrice())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/book/remove-all-books-with-categories.sql",
            "classpath:database/book/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBook_ValidId_ReturnsNoContentStatus() throws Exception {
        long idPassed = 2L;
        mockMvc.perform(delete(CONTROLLER_ENDPOINT + "/" + idPassed)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(
                        get(CONTROLLER_ENDPOINT + "/" + idPassed)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    private List<BookDtoWithoutCategoryIds> getThreeBookDtoWithoutCategoryIds() {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds(
                1L,
                "Fantasy Book 1",
                "Fantasy Author 1",
                "978-83-7506-729-3",
                new BigDecimal("40.00"),
                "Fantasy Description 1",
                "images.com/image1.jpg"));
        expected.add(new BookDtoWithoutCategoryIds(
                2L,
                "Fantasy Book 2",
                "Fantasy Author 2",
                "978-83-080-8011-5",
                new BigDecimal("45.50"),
                "Fantasy Description 2",
                "images.com/image2.jpg"));
        expected.add(new BookDtoWithoutCategoryIds(
                3L,
                "Drama Book",
                "Drama Author",
                "978-83-240-6401-4",
                new BigDecimal("69.50"),
                "Drama Description",
                "images.com/image3.jpg"));
        return expected;
    }

    private List<BookDtoWithoutCategoryIds> getTwoMatchingBookDtoWithoutCategoryIds() {
        List<BookDtoWithoutCategoryIds> expected = new ArrayList<>();
        expected.add(new BookDtoWithoutCategoryIds(
                1L,
                "Fantasy Book 1",
                "Fantasy Author 1",
                "978-83-7506-729-3",
                new BigDecimal("40.00"),
                "Fantasy Description 1",
                "images.com/image1.jpg"));
        expected.add(new BookDtoWithoutCategoryIds(
                2L,
                "Fantasy Book 2",
                "Fantasy Author 2",
                "978-83-080-8011-5",
                new BigDecimal("45.50"),
                "Fantasy Description 2",
                "images.com/image2.jpg"));
        return expected;
    }

    private CreateBookRequestDto createTestCreateBookRequestDto() {
        return new CreateBookRequestDto(
                "A new created book",
                "Author of a new created book",
                "1111111111111",
                new BigDecimal("70.00"),
                "Description of a new book",
                "images.com/image_new_book.jpg",
                Set.of(1L));
    }

    private BookDto getBookDtoFromRequest(Long id, CreateBookRequestDto requestDto) {
        return new BookDto(
                id,
                requestDto.title(),
                requestDto.author(),
                requestDto.isbn(),
                requestDto.price(),
                requestDto.description(),
                requestDto.coverImage(),
                requestDto.categoryIds());
    }
}
*/
