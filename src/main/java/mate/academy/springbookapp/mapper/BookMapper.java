package mate.academy.springbookapp.mapper;

import mate.academy.springbookapp.config.MapperConfig;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import mate.academy.springbookapp.model.Book;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    BookDto toDto(Book book);

    Book toModel(CreateBookRequestDto createBookRequestDto);
}
