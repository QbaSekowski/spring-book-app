package mate.academy.springbookapp.service.book;

import java.util.List;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto);
}
