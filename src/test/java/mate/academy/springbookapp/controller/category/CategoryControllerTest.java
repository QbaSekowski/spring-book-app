package mate.academy.springbookapp.controller.category;

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
import javax.sql.DataSource;
import lombok.SneakyThrows;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.category.CategoryDto;
import mate.academy.springbookapp.dto.category.CreateCategoryRequestDto;
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
public class CategoryControllerTest {
    protected static MockMvc mockMvc;
    private static final String BASE_ENDPOINT = "/api/categories";
    private static final String DB_PATH_ADD_THREE_CATEGORIES
            = "database/category/add-three-categories.sql";
    private static final String DB_PATH_DELETE_THREE_CATEGORIES
            = "database/category/delete-categories.sql";

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
        executeSqlScript(dataSource, DB_PATH_ADD_THREE_CATEGORIES);
    }

    @AfterAll
    static void afterAll(@Autowired DataSource dataSource) {
        executeSqlScript(dataSource, DB_PATH_DELETE_THREE_CATEGORIES);
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

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getAll_ThreeCategoriesInDb_ReturnsAllCategories() throws Exception {
        List<CategoryDto> expectedListOfCategories = new ArrayList<>();
        expectedListOfCategories.add(new CategoryDto(1L, "Drama", "Drama category"));
        expectedListOfCategories.add(new CategoryDto(2L, "Romance", "Romance category"));
        expectedListOfCategories.add(new CategoryDto(3L, "Thriller", "Thriller category"));
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto[] actualListOfBooks = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        assertNotNull(actualListOfBooks);
        assertEquals(3, actualListOfBooks.length);
        assertEquals(expectedListOfCategories, Arrays.stream(actualListOfBooks).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/delete-newly-created-category.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_CorrectRequestDto_ReturnsNewCategory() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Comedy", "Comedy category");
        CategoryDto expected = new CategoryDto(4L, requestDto.name(), requestDto.description());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        post(BASE_ENDPOINT)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    @Sql(scripts = {"classpath:database/category/delete-categories.sql",
            "classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql",
            "classpath:database/category/add-three-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_CorrectCategoryId_ReturnsCorrectCategories() throws Exception {
        long categoryId = 1L;
        List<BookDtoWithoutCategoryIds> expectedListOfBooks = getTwoBookDtoWithNoCategories();
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + categoryId + "/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actualListOfBooks = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertNotNull(actualListOfBooks);
        assertEquals(2, actualListOfBooks.length);
        assertEquals(expectedListOfBooks, Arrays.stream(actualListOfBooks).toList());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getCategoryById_CorrectId_ReturnsCategoryDto() throws Exception {
        Long categoryId = 3L;
        CategoryDto expectedCategory = new CategoryDto(categoryId, "Thriller",
                "Thriller category");
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT + "/"
                                + categoryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actualCategory = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);
        assertNotNull(actualCategory);
        assertEquals(expectedCategory, actualCategory);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/undo-category-update.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_CorrectId_ReturnsNoContentStatus() throws Exception {
        long categoryId = 2L;
        mockMvc.perform(delete(BASE_ENDPOINT + "/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(get(BASE_ENDPOINT + "/" + categoryId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/undo-category-update.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_CorrectRequestDtoAndId_ReturnsUpdatedCategory() throws Exception {
        Long categoryId = 2L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Updated name", "Updated description");
        CategoryDto expectedCategory = new CategoryDto(
                categoryId, requestDto.name(), requestDto.description());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put(BASE_ENDPOINT + "/" + categoryId)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actualCategory = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actualCategory.id());
        EqualsBuilder.reflectionEquals(expectedCategory, actualCategory, "id");
    }

    private List<BookDtoWithoutCategoryIds> getTwoBookDtoWithNoCategories() {
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
}

