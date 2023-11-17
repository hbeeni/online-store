package com.been.onlinestore.util;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.service.response.CategoryResponse;
import com.been.onlinestore.service.response.admin.AdminCategoryResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static java.time.LocalDateTime.now;

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

    public static AdminCategoryResponse createAdminCategoryResponse(Long id, String name, int productCount) {
        return AdminCategoryResponse.of(
                id,
                name,
                name + DESCRIPTION_SUFFIX,
                productCount,
                now(),
                "been",
                now(),
                "been"
        );
    }

    public static CategoryResponse createCategoryResponse(Long id, String name, int productCount) {
        return CategoryResponse.of(
                id,
                name,
                name + DESCRIPTION_SUFFIX,
                productCount
        );
    }
}
