package mate.academy.springbookapp.service.book;

import java.util.List;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto createBookRequestDto);

    BookDtoWithoutCategoryIds findById(Long id);

    List<BookDtoWithoutCategoryIds> findAll(Pageable pageable);

    BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> search(BookSearchParametersDto bookSearchParametersDto,
                                           Pageable pageable);

    List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable);

    BookDto findByIdWithCategories(Long id);

    List<BookDto> findAllWithCategories(Pageable pageable);
}
