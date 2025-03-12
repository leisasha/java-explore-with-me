package ru.practicum.ewm.dto.category;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.Category;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CategoryMapper {

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static Category newCategoryDtotoCategory(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }
}