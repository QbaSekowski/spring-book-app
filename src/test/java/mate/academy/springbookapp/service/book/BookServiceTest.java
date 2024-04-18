package mate.academy.springbookapp.service.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.BookMapper;
import mate.academy.springbookapp.model.Book;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.repository.book.BookRepository;
import mate.academy.springbookapp.repository.book.BookSpecificationBuilder;
import mate.academy.springbookapp.repository.book.spec.PriceSpecificationProvider;
import mate.academy.springbookapp.service.book.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final String BOOK_TITLE = "Title";
    private static final String BOOK_AUTHOR = "Author";
    private static final String BOOK_DESCRIPTION = "Description";
    private static final String BOOK_COVER_IMAGE = "image.com/image.png";
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("Save a new book with valid request - Returns saved book DTO")
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto(
                "66565-878787", new BigDecimal("15.80"), Set.of(1L, 2L));
        Book modelBook = createTestBook(
                1L, requestDto.isbn(), requestDto.price(), requestDto.categoryIds());
        BookDto expected = createTestBookDto(modelBook);
        when(bookMapper.toModel(requestDto)).thenReturn(modelBook);
        when(bookRepository.save(modelBook)).thenReturn(modelBook);
        when(bookMapper.toDto(modelBook)).thenReturn(expected);
        BookDto actual = bookService.save(requestDto);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books with valid pageable"
            + " - Returns all books DTOs without category IDs")
    void findAll_ValidPageable_ReturnsAllBookDtoWithoutCategoryIds() {
        Book modelBook = createTestBook(1L, "66565-878787", new BigDecimal("11.70"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategoryIds(modelBook);
        Book modelBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("16.70"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategoryIds(modelBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook, modelBook2);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(modelBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actual = bookService.findAll(pageable);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by valid ID - Returns book DTO without category IDs")
    void findById_ValidId_ReturnsBookDtoWithoutCategoryIds() {
        Long validId = 1L;
        Book modelBook = createTestBook(validId, "66565-878787",
                new BigDecimal("11.70"), Set.of(1L));
        BookDtoWithoutCategoryIds expected = createTestBookDtoWithoutCategoryIds(modelBook);
        when(bookRepository.findById(validId)).thenReturn(Optional.of(modelBook));
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(expected);
        BookDtoWithoutCategoryIds actual = bookService.findById(validId);
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(any());
        verify(bookMapper, times(1)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by invalid ID - Throws EntityNotFoundException")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 10L;
        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());
        String expected = "Can't find book by id: " + invalidId;
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class, () -> bookService.findById(invalidId));
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search books with valid pageable and search parameters"
            + " - Returns matching books DTOs without category IDs")
    void search_ValidPageableAndSearchParameters_ReturnsAllMatchingBookDtoWithoutCategoryIds() {
        Book modelBook = createTestBook(1L, "66565-878787", new BigDecimal("11.70"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategoryIds(modelBook);
        Book modelBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("24.70"), Set.of(2L));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategoryIds(modelBook2);
        String minPrice = "10.00";
        String maxPrice = "25.00";
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                null, null, null, "10", "15");
        Specification<Book> specification = Specification.where(new PriceSpecificationProvider()
                .getSpecification(new String[]{minPrice, maxPrice}));
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook, modelBook2);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookSpecificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(modelBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actual =
                bookService.search(bookSearchParametersDto, pageable);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookSpecificationBuilder, times(1)).build(any());
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Search books with valid pageable and empty search parameters"
            + " - Returns all books DTOs without category IDs")
    void search_ValidPageableAndEmptySearchParameters_ReturnsAllBookDtoWithoutCategoryIds() {
        Book modelBook = createTestBook(1L, "66565-878787",
                new BigDecimal("11.70"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategoryIds(modelBook);
        Book modelBook2 = createTestBook(2L, "66565-878787-1",
                new BigDecimal("244.70"), Set.of(32L));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategoryIds(modelBook2);
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                null, null, null, null, null);
        Specification<Book> specification = Specification.where(null);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook, modelBook2);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookSpecificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(modelBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actual =
                bookService.search(bookSearchParametersDto, pageable);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookSpecificationBuilder, times(1)).build(any());
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update book by valid ID and request"
            + " (or create new when ID doesn't exist) - Returns updated or created book DTO")
    void updateById_ValidIdAndCreateBookRequestDto_ReturnsUpdatedBook() {
        Long validId = 3L;
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto(
                "66565-878787", new BigDecimal("15.80"), Set.of(1L, 2L));
        Book modelBook = createTestBook(
                validId, requestDto.isbn(), requestDto.price(), requestDto.categoryIds());
        BookDto expected = createTestBookDto(modelBook);
        when(bookMapper.toModel(requestDto)).thenReturn(modelBook);
        when(bookRepository.save(modelBook)).thenReturn(modelBook);
        when(bookMapper.toDto(modelBook)).thenReturn(expected);
        BookDto actual = bookService.updateById(validId, requestDto);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books by category ID with valid category ID and pageable"
            + " - Returns all books DTOs without category IDs")
    void findAllByCategoryId_ValidCategoryIdAndPageable_ReturnsBookDtoWithoutCategoryIds() {
        Long validCategoryId = 1L;
        Book modelBook = createTestBook(
                1L, "66565-878787", new BigDecimal("11.70"), Set.of(validCategoryId));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategoryIds(modelBook);
        Book modelBook2 = createTestBook(
                2L, "66565-878787-1", new BigDecimal("19.70"), Set.of(validCategoryId));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategoryIds(modelBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook, modelBook2);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAllByCategoryId(validCategoryId, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(modelBook)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(modelBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expected = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actual =
                bookService.findAllByCategoryId(validCategoryId, pageable);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAllByCategoryId(any(), eq(pageable));
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by ID with categories by valid ID - Returns book DTO")
    void findByIdWithCategories_ValidId_ReturnsBookDto() {
        Long validId = 1L;
        Book modelBook = createTestBook(validId, "66565-878787",
                new BigDecimal("11.70"), Set.of(1L));
        BookDto expected = createTestBookDto(modelBook);
        when(bookRepository.findByIdWithCategories(validId)).thenReturn(Optional.of(modelBook));
        when(bookMapper.toDto(modelBook)).thenReturn(expected);
        BookDto actual = bookService.findByIdWithCategories(validId);
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verify(bookMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by ID with categories by invalid ID - Throws EntityNotFoundException")
    void findByIdWithCategories_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 10L;
        when(bookRepository.findByIdWithCategories(invalidId)).thenReturn(Optional.empty());
        String expected = "Can't find book by id: " + invalidId;
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findByIdWithCategories(invalidId));
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findByIdWithCategories(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Find all books with categories with valid pageable - Returns all books DTOs")
    void findAllWithCategories_ValidPageable_ReturnsAllBookDto() {
        Book modelBook = createTestBook(1L, "66565-878787", new BigDecimal("11.70"), Set.of(1L));
        BookDto bookDto = createTestBookDto(modelBook);
        Book modelBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("21.70"), Set.of(2L));
        BookDto bookDto2 = createTestBookDto(modelBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(modelBook, modelBook2);
        PageImpl<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        when(bookRepository.findAllWithCategories(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(modelBook)).thenReturn(bookDto);
        when(bookMapper.toDto(modelBook2)).thenReturn(bookDto2);
        List<BookDto> expected = List.of(bookDto, bookDto2);
        List<BookDto> actual = bookService.findAllWithCategories(pageable);
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAllWithCategories(pageable);
        verify(bookMapper, times(2)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private Book createTestBook(Long id, String isbn, BigDecimal price, Set<Long> categoryIds) {
        Book book = new Book();
        book.setId(id);
        book.setTitle(BOOK_TITLE);
        book.setAuthor(BOOK_AUTHOR);
        book.setIsbn(isbn);
        book.setPrice(price);
        book.setDescription(BOOK_DESCRIPTION);
        book.setCoverImage(BOOK_COVER_IMAGE);
        book.setCategories(categoryIds.stream()
                .map(Category::new)
                .collect(Collectors
                        .toSet()));
        return book;
    }

    private BookDto createTestBookDto(Book modelBook) {
        BookDto bookDto = new BookDto();
        bookDto.setId(modelBook.getId());
        bookDto.setTitle(modelBook.getTitle());
        bookDto.setAuthor(modelBook.getAuthor());
        bookDto.setIsbn(modelBook.getIsbn());
        bookDto.setPrice(modelBook.getPrice());
        bookDto.setDescription(modelBook.getDescription());
        bookDto.setCoverImage(modelBook.getCoverImage());
        bookDto.setCategoryIds(modelBook.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        return bookDto;
    }

    private BookDtoWithoutCategoryIds createTestBookDtoWithoutCategoryIds(Book modelBook) {
        return new BookDtoWithoutCategoryIds(
                modelBook.getId(),
                modelBook.getTitle(),
                modelBook.getAuthor(),
                modelBook.getIsbn(),
                modelBook.getPrice(),
                modelBook.getDescription(),
                modelBook.getCoverImage());
    }

    private CreateBookRequestDto createTestCreateBookRequestDto(
            String isbn, BigDecimal price, Set<Long> categoryIds) {
        return new CreateBookRequestDto(
                BOOK_TITLE,
                BOOK_AUTHOR,
                isbn,
                price,
                BOOK_DESCRIPTION,
                BOOK_COVER_IMAGE,
                categoryIds);
    }
}

