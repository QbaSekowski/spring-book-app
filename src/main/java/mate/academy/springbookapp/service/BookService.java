package mate.academy.springbookapp.service;

import java.util.List;
import mate.academy.springbookapp.dto.BookDto;
import mate.academy.springbookapp.dto.BookSearchParametersDto;
import mate.academy.springbookapp.dto.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto bookSearchParametersDto);
}
