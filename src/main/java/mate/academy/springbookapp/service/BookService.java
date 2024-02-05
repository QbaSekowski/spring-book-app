package mate.academy.springbookapp.service;

import java.util.List;
import mate.academy.springbookapp.dto.BookDto;
import mate.academy.springbookapp.dto.CreateBookRequestDto;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDto findById(Long id);

    List<BookDto> findAll();

    BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteById(Long id);
}
