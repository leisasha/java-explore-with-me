package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.CategoryMapper;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<Category> page = categoryRepository.findAll(pageable);

        return page.getContent().stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return CategoryMapper.toCategoryDto(category);
    }

    public CategoryDto createCategory(NewCategoryDto categoryDto) {
        return CategoryMapper.toCategoryDto(
                categoryRepository.save(CategoryMapper.newCategoryDtotoCategory(categoryDto))
        );
    }

    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
