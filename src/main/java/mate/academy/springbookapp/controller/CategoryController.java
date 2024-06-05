package mate.academy.springbookapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.springbookapp.dto.category.CategoryDto;
import mate.academy.springbookapp.dto.category.CreateCategoryRequestDto;
import mate.academy.springbookapp.service.book.BookService;
import mate.academy.springbookapp.service.category.CategoryService;
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

@Tag(name = "Category management", description = "Endpoints for managing categories")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/categories")
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a new category", description = "Create a new category by providing "
            + "its name and description")
    public CategoryDto createCategory(
            @RequestBody @Valid CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryService.save(createCategoryRequestDto);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all categories", description = "Get a list of all available categories from DB")
    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get a category by ID", description = "Get all details of a specific category "
            + "by providing its ID")
    public CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.getById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a category by ID", description = "Update a category with given ID "
            + "by providing its name and description")
    public CategoryDto updateCategory(@PathVariable Long id,
                                      @RequestBody @Valid CreateCategoryRequestDto
                                              createCategoryRequestDto) {
        return categoryService.update(id, createCategoryRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a category by ID", description = "Remove a category by its ID from DB")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

    @GetMapping("/{id}/books")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get books by category ID",
            description = "Get a list of all books from a category of a specific ID")
    public List<BookDtoWithoutCategoryIds> getBooksByCategoryId(@PathVariable Long id,
                                                                Pageable pageable) {
        return bookService.findAllByCategoryId(id, pageable);
    }
}
