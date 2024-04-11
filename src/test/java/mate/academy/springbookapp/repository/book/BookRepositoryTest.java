package mate.academy.springbookapp.repository.book;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import mate.academy.springbookapp.model.Book;
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
    @DisplayName("Find all books by valid category ID")
    @Sql(scripts = {"classpath:database/books/add-three-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/books/remove-all-books-with-categories.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByCategoryId_ValidCategoryId_ReturnsTwoBooks() {
        List<Book> actual = bookRepository.findAllByCategoryId(1L,
                        PageRequest.of(0, 10))
                .stream()
                .toList();
        assertEquals(2, actual.size());
    }
}
