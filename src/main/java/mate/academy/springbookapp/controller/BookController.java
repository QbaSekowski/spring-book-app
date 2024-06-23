package mate.academy.springbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.book.BookDto;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.book.BookSearchParametersDto;
import mate.academy.springbookapp.dto.book.CreateBookRequestDto;
import mate.academy.springbookapp.service.book.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all books", description = "Get a list of all available books from DB")
    public List<BookDtoWithoutCategoryIds> getAll(Pageable pageable) {
        return bookService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a book by ID", description = "Get all details of a specific book "
            + "by providing its ID")
    public BookDtoWithoutCategoryIds getBookById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book by providing "
            + "its title, author, isbn, price, description, cover image and category ID")
    public BookDto createBook(@RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.save(createBookRequestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a book by ID",
            description = "Update a book with given ID by providing its title, "
            + "author, isbn, price, description, cover image and category ID")
    public BookDto updateBookById(@PathVariable Long id,
                                  @RequestBody @Valid CreateBookRequestDto createBookRequestDto) {
        return bookService.updateById(id, createBookRequestDto);
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search books by parameters",
            description = "Get a list of books by given parameters: "
                    + "author, title, isbn, minPrice, maxPrice")
    public List<BookDtoWithoutCategoryIds> search(Pageable pageable,
                                                  BookSearchParametersDto bookSearchParametersDto) {
        return bookService.search(bookSearchParametersDto, pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a book", description = "Remove a book by its ID from DB")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
