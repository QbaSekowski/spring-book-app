package mate.academy.springbookapp.repository;

import java.util.List;
import mate.academy.springbookapp.model.Book;

public interface BookRepository {
    Book save(Book book);

    List<Book> findAll();
}
