package mate.academy.springbookapp.repository.book;

import java.util.Optional;
import mate.academy.springbookapp.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("SELECT b FROM Book b JOIN FETCH b.categories c WHERE c.id = :categoryId")
    Page<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @Query("FROM Book b JOIN FETCH b.categories WHERE b.id = :id")
    Optional<Book> findByIdWithCategories(Long id);

    @Query("SELECT b FROM Book b JOIN FETCH b.categories")
    Page<Book> findAllWithCategories(Pageable pageable);
}
