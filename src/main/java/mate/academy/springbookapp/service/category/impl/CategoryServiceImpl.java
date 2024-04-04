package mate.academy.springbookapp.service.category.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.springbookapp.dto.category.CategoryDto;
import mate.academy.springbookapp.dto.category.CreateCategoryRequestDto;
import mate.academy.springbookapp.exception.EntityNotFoundException;
import mate.academy.springbookapp.mapper.CategoryMapper;
import mate.academy.springbookapp.model.Category;
import mate.academy.springbookapp.repository.category.CategoryRepository;
import mate.academy.springbookapp.service.category.CategoryService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        return categoryMapper.toDto(categoryRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find category with id: " + id)));
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto createCategoryRequestDto) {
        return categoryMapper.toDto(categoryRepository
                .save(categoryMapper.toModel(createCategoryRequestDto)));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto createCategoryRequestDto) {
        Category updatedCategory = categoryMapper.toModel(createCategoryRequestDto);
        updatedCategory.setId(id);
        return categoryMapper.toDto(categoryRepository.save(updatedCategory));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
