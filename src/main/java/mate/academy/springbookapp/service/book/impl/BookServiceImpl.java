package mate.academy.springbookapp.service.book.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.BookMapper;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.repository.book.BookRepository;
import mate.academy.springbookapp.repository.book.BookSpecificationBuilder;
import mate.academy.springbookapp.service.book.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
    public List<BookDto> findAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
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
    public List<BookDtoWithoutCategoryIds> search(BookSearchParametersDto bookSearchParametersDto,
                                                  Pageable pageable) {
        return bookRepository.findAll(bookSpecificationBuilder
                        .build(bookSearchParametersDto), pageable)
                .stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }

    @Override
    public BookDto findByIdWithCategories(Long id) {
        return bookMapper.toDto(bookRepository.findByIdWithCategories(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find book by id: " + id)));
    }

    @Override
    public List<BookDto> findAllWithCategories(Pageable pageable) {
        return bookRepository.findAllWithCategories(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDtoWithoutCategoryIds> findAllByCategoryId(Long id, Pageable pageable) {
        return bookRepository.findAllByCategoryId(id, pageable).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
