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
            = "database/category/delete-three-categories.sql";

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
        List<CategoryDto> expected = new ArrayList<>();
        expected.add(new CategoryDto(1L, "Drama", "Drama category"));
        expected.add(new CategoryDto(2L, "Romance", "Romance category"));
        expected.add(new CategoryDto(3L, "Thriller", "Thriller category"));
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto[].class);
        assertNotNull(actual);
        assertEquals(3, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/delete-created-category.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createCategory_ValidRequestDto_ReturnsNewCategory() throws Exception {
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
    @Sql(scripts = {"classpath:database/category/delete-three-categories.sql",
            "classpath:database/book/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/remove-all-books-with-categories.sql",
            "classpath:database/category/add-three-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getBooksByCategoryId_ValidCategoryId_ReturnsAllMatchingCategories() throws Exception {
        long validCategoryId = 1L;
        List<BookDtoWithoutCategoryIds> expected = getTwoBookDtoWithoutCategoryIds();
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + validCategoryId + "/books")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        BookDtoWithoutCategoryIds[] actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), BookDtoWithoutCategoryIds[].class);
        assertNotNull(actual);
        assertEquals(2, actual.length);
        assertEquals(expected, Arrays.stream(actual).toList());
    }

    @WithMockUser(username = "user", authorities = {"USER"})
    @Test
    void getCategoryById_ValidId_ReturnsCategoryDto() throws Exception {
        Long validId = 3L;
        CategoryDto expected = new CategoryDto(validId, "Thriller", "Thriller category");
        MvcResult result = mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + validId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CategoryDto.class);
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/revert-category-updated.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteCategory_ValidId_ReturnsNoContentStatus() throws Exception {
        long idPassed = 2L;
        mockMvc.perform(delete(BASE_ENDPOINT + "/" + idPassed)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andReturn();
        mockMvc.perform(
                        get(BASE_ENDPOINT + "/" + idPassed)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Test
    @Sql(scripts = {"classpath:database/category/revert-category-updated.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateCategory_ValidRequestDtoAndId_ReturnsUpdatedCategory() throws Exception {
        Long idPassed = 2L;
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Updated name", "Updated description");
        CategoryDto expected = new CategoryDto(
                idPassed, requestDto.name(), requestDto.description());
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                        put(BASE_ENDPOINT + "/" + idPassed)
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        CategoryDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryDto.class);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    private List<BookDtoWithoutCategoryIds> getTwoBookDtoWithoutCategoryIds() {
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

