package mate.academy.springbookapp.service.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.springbookapp.dto.category.CategoryDto;
import mate.academy.springbookapp.dto.category.CreateCategoryRequestDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.CategoryMapper;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.repository.category.CategoryRepository;
import mate.academy.springbookapp.service.category.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    private static final String CATEGORY_NAME = "Category";
    private static final String CATEGORY_DESCRIPTION = "Description";
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Find category by invalid ID - Throws EntityNotFoundException")
    void findById_InvalidId_ThrowsEntityNotFoundException() {
        Long invalidId = 100L;
        String expected = "Can't find category with id: " + invalidId;
        when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(invalidId));
        String actual = exception.getMessage();
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by valid ID - Returns category DTO")
    void findById_ValidId_ReturnsCategoryDto() {
        Long validId = 1L;
        Category modelCategory = createTestCategory(validId, CATEGORY_NAME);
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryRepository.findById(validId)).thenReturn(Optional.of(modelCategory));
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);
        CategoryDto actual = categoryService.getById(validId);
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Save new category with valid request - Returns saved category DTO")
    void save_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category modelCategory = createTestCategory(1L, requestDto.name());
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(modelCategory);
        when(categoryRepository.save(modelCategory)).thenReturn(modelCategory);
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);
        CategoryDto actual = categoryService.save(requestDto);
        assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toModel(any());
        verify(categoryRepository, times(1)).save(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Update category by valid ID and request "
            + "(or create new when ID doesn't exist) - Returns updated or created category DTO")
    void update_ValidCreateCategoryRequestDto_ReturnsCategoryDto() {
        Long newId = 10L;
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category modelCategory = createTestCategory(newId, requestDto.name());
        CategoryDto expected =
                createTestCategoryDto(modelCategory.getId(), modelCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(modelCategory);
        when(categoryRepository.save(modelCategory)).thenReturn(modelCategory);
        when(categoryMapper.toDto(modelCategory)).thenReturn(expected);
        CategoryDto actual = categoryService.update(newId, requestDto);
        assertEquals(expected, actual);
        verify(categoryMapper, times(1)).toModel(any());
        verify(categoryRepository, times(1)).save(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    private Category createTestCategory(Long id, String name) {
        Category category = new Category(id);
        category.setName(name);
        category.setDescription(CATEGORY_DESCRIPTION);
        return category;
    }

    private CategoryDto createTestCategoryDto(Long id, String name) {
        return new CategoryDto(id, name, CATEGORY_DESCRIPTION);
    }

    private CreateCategoryRequestDto createTestCreateCategoryRequestDto() {
        return new CreateCategoryRequestDto(CATEGORY_NAME, CATEGORY_DESCRIPTION);
    }
}
