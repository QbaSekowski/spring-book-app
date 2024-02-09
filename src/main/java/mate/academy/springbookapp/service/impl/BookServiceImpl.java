package mate.academy.springbookapp.service.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import mate.academy.springbookapp.dto.BookDto;
import mate.academy.springbookapp.dto.BookSearchParametersDto;
import mate.academy.springbookapp.dto.CreateBookRequestDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.BookMapper;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.repository.book.BookRepository;
import mate.academy.springbookapp.repository.book.BookSpecificationBuilder;
import mate.academy.springbookapp.service.BookService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto createBookRequestDto) {
        Book book = bookMapper.toModel(createBookRequestDto);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toDto(savedBook);
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto updateById(Long id, CreateBookRequestDto createBookRequestDto) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Can't find book by id: " + id);
        }
        Book book = bookMapper.toModel(createBookRequestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto bookSearchParametersDto) {
        return bookRepository.findAll(bookSpecificationBuilder.build(bookSearchParametersDto))
                .stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
