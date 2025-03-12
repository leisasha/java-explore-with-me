package ru.practicum.ewm.controller.pub;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.service.category.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategory(@Positive @PathVariable Long catId) {
        return categoryService.getCategoryById(catId);
    }
}
