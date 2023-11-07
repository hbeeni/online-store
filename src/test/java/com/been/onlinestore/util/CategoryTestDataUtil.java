package com.been.onlinestore.util;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.dto.CategoryDto;
import org.springframework.test.util.ReflectionTestUtils;

public class CategoryTestDataUtil {

    private static Long sequence = 0L;
    public static final String DESCRIPTION_SUFFIX = " description";

    public static Category createCategory(Long id, String name) {
        Category category = Category.of(name, name + DESCRIPTION_SUFFIX);
        ReflectionTestUtils.setField(category, "id", id);
        return category;
    }

    public static Category createCategory(String name) {
        Category category = Category.of(name, name + DESCRIPTION_SUFFIX);
        ReflectionTestUtils.setField(category, "id", ++sequence);
        return category;
    }

    public static CategoryDto createCategoryDto(String name) {
        return createCategoryDto(1L, name);
    }

    public static CategoryDto createCategoryDto(Long id, String name) {
        return CategoryDto.of(id, name, name + DESCRIPTION_SUFFIX, 0);
    }
}
