package com.been.onlinestore.util;

import org.springframework.test.util.ReflectionTestUtils;

import com.been.onlinestore.domain.Category;

public class CategoryTestDataUtil {

	public static final String DESCRIPTION_SUFFIX = " description";
	private static Long sequence = 0L;

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
}
