/*
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
    @DisplayName("Return book DTO after correct saving of a new book")
    void save_CorrectCreateBookRequestDto_ReturnsBookDto() {
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto(
                "66565-878787", new BigDecimal("21.50"), Set.of(1L, 2L));
        Book testBook = createTestBook(
                1L, requestDto.isbn(), requestDto.price(), requestDto.categoryIds());
        BookDto expectedBook = createTestBookDto(testBook);
        when(bookMapper.toModel(requestDto)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(expectedBook);
        BookDto actualBook = bookService.save(requestDto);
        assertEquals(expectedBook, actualBook);
        verify(bookMapper, times(1)).toDto(any());
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Return all books with correct pageable")
    void findAll_CorrectPageable_ReturnsAllBookDtoWithNoCategories() {
        Book testBook1 = createTestBook(1L, "66565-878787", new BigDecimal("17.80"), Set.of(1L));
        BookDtoWithoutCategoryIds testBookDto = createTestBookDtoWithoutCategories(testBook1);
        Book testBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("14.10"), Set.of(1L));
        BookDtoWithoutCategoryIds testBookDto2 = createTestBookDtoWithoutCategories(testBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> listOfTestBooks = List.of(testBook1, testBook2);
        PageImpl<Book> testBooksPage
                = new PageImpl<>(listOfTestBooks, pageable, listOfTestBooks.size());
        when(bookRepository.findAll(pageable)).thenReturn(testBooksPage);
        when(bookMapper.toDtoWithoutCategories(testBook1)).thenReturn(testBookDto);
        when(bookMapper.toDtoWithoutCategories(testBook2)).thenReturn(testBookDto2);
        List<BookDtoWithoutCategoryIds> expectedListOfBooks = List.of(testBookDto, testBookDto2);
        List<BookDtoWithoutCategoryIds> actualListOfBooks = bookService.findAll(pageable);
        assertEquals(2, actualListOfBooks.size());
        assertEquals(expectedListOfBooks, actualListOfBooks);
        verify(bookRepository, times(1)).findAll(pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find a book providing correct book ID")
    void findById_CorrectId_ReturnsBookDtoWithNoCategories() {
        Long correctBookId = 1L;
        Book testBook = createTestBook(correctBookId, "66565-878787",
                new BigDecimal("19.50"), Set.of(1L));
        BookDtoWithoutCategoryIds expectedBook = createTestBookDtoWithoutCategories(testBook);
        when(bookRepository.findById(correctBookId)).thenReturn(Optional.of(testBook));
        when(bookMapper.toDtoWithoutCategories(testBook)).thenReturn(expectedBook);
        BookDtoWithoutCategoryIds actualBook = bookService.findById(correctBookId);
        assertNotNull(actualBook);
        assertEquals(expectedBook, actualBook);
        verify(bookRepository, times(1)).findById(any());
        verify(bookMapper, times(1)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Try to find a book with incorrect ID and throw EntityNotFoundException")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long incorrectBookId = 30L;
        when(bookRepository.findById(incorrectBookId)).thenReturn(Optional.empty());
        String expectedInfo = "Can't find book by id: " + incorrectBookId;
        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> bookService.findById(incorrectBookId));
        String actualInfo = exception.getMessage();
        assertEquals(expectedInfo, actualInfo);
        verify(bookRepository, times(1)).findById(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Search books with correct search parameters")
    void search_CorrectSearchParameters_ReturnsCorrectMatchingBookDtoWithNoCategories() {
        Book testBook1 = createTestBook(1L, "66565-878787", new BigDecimal("10.40"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategories(testBook1);
        Book testBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("25.40"), Set.of(2L));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategories(testBook2);
        String minPrice = "5.00";
        String maxPrice = "30.00";
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                null, null, null, minPrice, maxPrice);
        Specification<Book> specification = Specification.where(new PriceSpecificationProvider()
                .getSpecification(new String[]{minPrice, maxPrice}));
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> listOfTestBooks = List.of(testBook1, testBook2);
        PageImpl<Book> booksPage
                = new PageImpl<>(listOfTestBooks, pageable, listOfTestBooks.size());
        when(bookSpecificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(testBook1)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(testBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expectedListOfBooks = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actualListOfBooks =
                bookService.search(bookSearchParametersDto, pageable);
        int expectedNumberOfBooks = 2;
        assertEquals(expectedNumberOfBooks, actualListOfBooks.size());
        assertEquals(expectedListOfBooks, actualListOfBooks);
        verify(bookSpecificationBuilder, times(1)).build(any());
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Search books with empty search parameters")
    void search_CorrectEmptySearchParameters_ReturnsAllBookDtoWithNoCategories() {
        Book testBook1 = createTestBook(1L, "66565-878787",
                new BigDecimal("10.40"), Set.of(1L));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategories(testBook1);
        Book testBook2 = createTestBook(2L, "66565-878787-1",
                new BigDecimal("244.70"), Set.of(32L));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategories(testBook2);
        BookSearchParametersDto bookSearchParametersDto = new BookSearchParametersDto(
                null, null, null, null, null);
        Specification<Book> specification = Specification.where(null);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> listOfTestBooks = List.of(testBook1, testBook2);
        PageImpl<Book> booksPage
                = new PageImpl<>(listOfTestBooks, pageable, listOfTestBooks.size());
        when(bookSpecificationBuilder.build(bookSearchParametersDto)).thenReturn(specification);
        when(bookRepository.findAll(specification, pageable)).thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(testBook1)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(testBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expectedListOfBooks = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actualListOfBooks =
                bookService.search(bookSearchParametersDto, pageable);
        int expectedNumberOfBooks = 2;
        assertEquals(expectedNumberOfBooks, actualListOfBooks.size());
        assertEquals(expectedListOfBooks, actualListOfBooks);
        verify(bookSpecificationBuilder, times(1)).build(any());
        verify(bookRepository, times(1)).findAll(specification, pageable);
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookSpecificationBuilder, bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Update a book successfully or create a new one")
    void updateBookById_CorrectIdAndCreateBookRequestDto_ReturnsUpdatedBook() {
        Long correctBookId = 3L;
        CreateBookRequestDto requestDto = createTestCreateBookRequestDto(
                "66565-878787", new BigDecimal("16.20"), Set.of(1L, 2L));
        Book testBook = createTestBook(
                correctBookId, requestDto.isbn(), requestDto.price(), requestDto.categoryIds());
        BookDto expectedBook = createTestBookDto(testBook);
        when(bookMapper.toModel(requestDto)).thenReturn(testBook);
        when(bookRepository.save(testBook)).thenReturn(testBook);
        when(bookMapper.toDto(testBook)).thenReturn(expectedBook);
        BookDto actualBook = bookService.updateById(correctBookId, requestDto);
        assertEquals(expectedBook, actualBook);
        verify(bookRepository, times(1)).save(any());
        verify(bookMapper, times(1)).toDto(any());
        verify(bookMapper, times(1)).toModel(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find all books providing a correct category ID")
    void findAllBooksByCategoryId_CorrectCategoryId_ReturnsBookDtoWithNoCategories() {
        Long correctCategoryId = 1L;
        Book testBook = createTestBook(1L, "66565-878787",
                new BigDecimal("14.60"), Set.of(correctCategoryId));
        BookDtoWithoutCategoryIds bookDto = createTestBookDtoWithoutCategories(testBook);
        Book testBook2 = createTestBook(2L, "66565-878787-1",
                new BigDecimal("25.70"), Set.of(correctCategoryId));
        BookDtoWithoutCategoryIds bookDto2 = createTestBookDtoWithoutCategories(testBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> listOfTestBooks = List.of(testBook, testBook2);
        PageImpl<Book> booksPage
                = new PageImpl<>(listOfTestBooks, pageable, listOfTestBooks.size());
        when(bookRepository.findAllBooksHavingCategoryById(correctCategoryId, pageable))
                .thenReturn(booksPage);
        when(bookMapper.toDtoWithoutCategories(testBook)).thenReturn(bookDto);
        when(bookMapper.toDtoWithoutCategories(testBook2)).thenReturn(bookDto2);
        List<BookDtoWithoutCategoryIds> expectedListOfBooks = List.of(bookDto, bookDto2);
        List<BookDtoWithoutCategoryIds> actualListOfBooks =
                bookService.findAllByCategoryId(correctCategoryId, pageable);
        assertEquals(2, actualListOfBooks.size());
        assertEquals(expectedListOfBooks, actualListOfBooks);
        verify(bookRepository, times(1)).findAllBooksHavingCategoryById(any(), eq(pageable));
        verify(bookMapper, times(2)).toDtoWithoutCategories(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by providing correct ID")
    void findBookByIdWithCategories_CorrectId_ReturnsBookDto() {
        Long correctId = 1L;
        Book testBook = createTestBook(correctId, "66565-878787",
                new BigDecimal("14.20"), Set.of(1L));
        BookDto expectedBook = createTestBookDto(testBook);
        when(bookRepository.findBookByIdHavingCategories(correctId))
                .thenReturn(Optional.of(testBook));
        when(bookMapper.toDto(testBook)).thenReturn(expectedBook);
        BookDto actualBook = bookService.findByIdWithCategories(correctId);
        assertEquals(expectedBook, actualBook);
        verify(bookRepository, times(1)).findBookByIdHavingCategories(any());
        verify(bookMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Find book by providing incorrect ID and throw EntityNotFoundException")
    void findBookByIdWithCategories_IncorrectId_ThrowsEntityNotFoundException() {
        Long incorrectId = 70L;
        when(bookRepository.findBookByIdHavingCategories(incorrectId)).thenReturn(Optional.empty());
        String expectedInfo = "Can't find book by id: " + incorrectId;
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.findByIdWithCategories(incorrectId));
        String actualInfo = exception.getMessage();
        assertEquals(expectedInfo, actualInfo);
        verify(bookRepository, times(1)).findBookByIdHavingCategories(any());
        verifyNoMoreInteractions(bookRepository);
    }

    @Test
    @DisplayName("Find all books with categories")
    void findAllBooksWithCategories_CorrectPageable_ReturnsAllBookDto() {
        Book testBooks1 = createTestBook(1L, "66565-878787", new BigDecimal("18.40"), Set.of(1L));
        BookDto bookDto = createTestBookDto(testBooks1);
        Book testBook2 = createTestBook(2L, "66565-878787-1", new BigDecimal("26.10"), Set.of(2L));
        BookDto bookDto2 = createTestBookDto(testBook2);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> listOfTestBooks = List.of(testBooks1, testBook2);
        PageImpl<Book> booksPage
                = new PageImpl<>(listOfTestBooks, pageable, listOfTestBooks.size());
        when(bookRepository.findAllBooksHavingCategories(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(testBooks1)).thenReturn(bookDto);
        when(bookMapper.toDto(testBook2)).thenReturn(bookDto2);
        List<BookDto> expectedListOfTestBooks = List.of(bookDto, bookDto2);
        List<BookDto> actualListOfTestBooks = bookService.findAllWithCategories(pageable);
        assertEquals(2, actualListOfTestBooks.size());
        assertEquals(expectedListOfTestBooks, actualListOfTestBooks);
        verify(bookRepository, times(1)).findAllBooksHavingCategories(pageable);
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

    private BookDtoWithoutCategoryIds createTestBookDtoWithoutCategories(Book modelBook) {
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

*/
