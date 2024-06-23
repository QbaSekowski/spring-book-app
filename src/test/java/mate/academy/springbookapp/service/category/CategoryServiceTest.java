/*
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
    @DisplayName("Find category by providing incorrect ID and throw EntityNotFoundException")
    void findCategoryById_IncorrectId_ThrowsEntityNotFoundException() {
        Long incorrectId = 100L;
        String expectedInfo = "Can't find category with id: " + incorrectId;
        when(categoryRepository.findById(incorrectId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> categoryService.getById(incorrectId));
        String actualInfo = exception.getMessage();
        assertEquals(expectedInfo, actualInfo);
        verify(categoryRepository, times(1)).findById(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Find category by providing correct ID")
    void findCategoryById_CorrectId_ReturnsCategoryDto() {
        Long correctId = 1L;
        Category testCategory = createTestCategory(correctId, CATEGORY_NAME);
        CategoryDto expectedCategory =
                createTestCategoryDto(testCategory.getId(), testCategory.getName());
        when(categoryRepository.findById(correctId)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toDto(testCategory)).thenReturn(expectedCategory);
        CategoryDto actualCategory = categoryService.getById(correctId);
        assertNotNull(actualCategory);
        assertEquals(expectedCategory, actualCategory);
        verify(categoryRepository, times(1)).findById(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Save a new category successfully")
    void save_CorrectCreateCategoryRequestDto_ReturnsCategoryDto() {
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category testCategory = createTestCategory(1L, requestDto.name());
        CategoryDto expectedCategory =
                createTestCategoryDto(testCategory.getId(), testCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDto(testCategory)).thenReturn(expectedCategory);
        CategoryDto actualCategory = categoryService.save(requestDto);
        assertEquals(expectedCategory, actualCategory);
        verify(categoryMapper, times(1)).toModel(any());
        verify(categoryRepository, times(1)).save(any());
        verify(categoryMapper, times(1)).toDto(any());
        verifyNoMoreInteractions(categoryMapper, categoryRepository);
    }

    @Test
    @DisplayName("Update category successfully or create a new one")
    void update_CorrectCreateCategoryRequestDto_ReturnsCategoryDto() {
        Long categoryId = 15L;
        CreateCategoryRequestDto requestDto = createTestCreateCategoryRequestDto();
        Category testCategory = createTestCategory(categoryId, requestDto.name());
        CategoryDto expectedCategory =
                createTestCategoryDto(testCategory.getId(), testCategory.getName());
        when(categoryMapper.toModel(requestDto)).thenReturn(testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toDto(testCategory)).thenReturn(expectedCategory);
        CategoryDto actualCategory = categoryService.update(categoryId, requestDto);
        assertEquals(expectedCategory, actualCategory);
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
*/
