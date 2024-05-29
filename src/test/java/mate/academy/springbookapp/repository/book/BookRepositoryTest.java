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
    @DisplayName("Find all books in the database")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllWithCategories_ThreeBooksInDB_ReturnsThreeBooks() {
        int expectedNumber = 3;
        List<Book> actualList = bookRepository.findAllBooksHavingCategories(
                        PageRequest.of(0, 10)).stream()
                .toList();
        assertEquals(expectedNumber, actualList.size());
    }

    @Test
    @DisplayName("Find a book by providing an incorrect book ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_IncorrectBookId_ReturnsEmpty() {
        Long incorrectId = 40L;
        Optional<Book> expectedBook = Optional.empty();
        Optional<Book> actualBook = bookRepository.findBookByIdHavingCategories(incorrectId);
        assertEquals(expectedBook, actualBook);
    }

    @Test
    @DisplayName("Find a book by providing a correct book ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findByIdWithCategories_CorrectBookId_ReturnsCorrectBook() {
        Category expectedCategory = new Category();
        expectedCategory.setId(1L);
        expectedCategory.setName("Fantasy");
        expectedCategory.setDescription("Fantasy category");
        Long validId = 1L;
        Book expectedBook = new Book();
        expectedBook.setId(validId);
        expectedBook.setTitle("Fantasy Book 1");
        expectedBook.setAuthor("Fantasy Author 1");
        expectedBook.setIsbn("978-83-7506-729-3");
        expectedBook.setPrice(new BigDecimal("40.00"));
        expectedBook.setDescription("Fantasy Description 1");
        expectedBook.setCoverImage("images.com/image1.jpg");
        expectedBook.setCategories(Set.of(expectedCategory));
        Optional<Book> actualBook = bookRepository.findBookByIdHavingCategories(validId);
        assertFalse(actualBook.isEmpty());
        assertThat(actualBook.get()).usingRecursiveComparison().isEqualTo(expectedBook);
    }

    @Test
    @DisplayName("Find all books with correct category ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_CorrectCategoryId_ReturnsTwoBooks() {
        Long correctCategoryId = 1L;
        int expectedNumber = 2;
        List<Book> actualListOfBooks = bookRepository.findAllBooksHavingCategoryById(correctCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(expectedNumber, actualListOfBooks.size());
    }

    @Test
    @DisplayName("Find all books with incorrect category ID")
    @Sql(scripts = {"classpath:database/book/add-three-books.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/book/delete-all-books.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_IncorrectCategoryId_ReturnsEmptyListOfBooks() {
        Long notCorrectCategoryId = 60L;
        List<Book> expectedListOfBooks = new ArrayList<>();
        int expectedNumber = 0;
        List<Book> actualListOfBooks = bookRepository.findAllBooksHavingCategoryById(
                        notCorrectCategoryId,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(expectedNumber, actualListOfBooks.size());
        assertEquals(expectedListOfBooks, actualListOfBooks);
    }
}
