package mate.academy.springbookapp.repository.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find all books with categories")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllWithCategories_ThreeBooksInDB_ReturnsThreeBooks() {
        int expected = 3;
        List<Book> actualList = bookRepository.findAllBooksHavingCategories(
                        PageRequest.of(0, 10)).stream()
                .toList();
        assertEquals(expected, actualList.size());
    }

    @Test
    @DisplayName("Find book by invalid ID with categories")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_InvalidBookId_ReturnsEmptyOptional() {
        Long notCorrectId = 40L;
        Optional<Book> expected = Optional.empty();
        Optional<Book> actual = bookRepository.findBookByIdHavingCategories(notCorrectId);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Find book by valid ID with categories")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_ValidBookId_ReturnsOneBook() {
        Category expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Fantasy");
        expectedCategory.setDescription("Fantasy category");
        Long validId = 1L;
        Book expected = new Book();
        expected.setId(validId);
        expected.setTitle("Fantasy Book 1");
        expected.setAuthor("Fantasy Author 1");
        expected.setIsbn("978-83-7506-729-3");
        expected.setPrice(new BigDecimal("40.00"));
        expected.setDescription("Fantasy Description 1");
        expected.setCoverImage("images.com/image1.jpg");
        expected.setCategories(Set.of(expectedCategory));
        Optional<Book> actual = bookRepository.findBookByIdHavingCategories(validId);
        assertFalse(actual.isEmpty());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("Find all books by valid category ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnsTwoBooks() {
        Long correctCategoryId = 1L;
        int expected = 2;
        List<Book> actual = bookRepository.findAllBooksHavingCategoryById(correctCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(expected, actual.size());
    }

    @Test
    @DisplayName("Find all books by invalid category ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_InvalidCategoryId_ReturnsEmptyListOfBooks() {
        Long notCorrectCategoryId = 10L;
        List<Book> expected = new ArrayList<>();
        List<Book> actual = bookRepository.findAllBooksHavingCategoryById(
                        notCorrectCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(0, actual.size());
        assertEquals(expected, actual);
    }
}
