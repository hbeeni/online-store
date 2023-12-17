package com.been.onlinestore.service;

import static com.been.onlinestore.util.CategoryTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.service.dto.response.CategoryResponse;

@DisplayName("비즈니스 로직 - 카테고리")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private CategoryService sut;

	@DisplayName("판매 중인 상품이 존재하는 모든 카테고리 정보를 반환한다.")
	@Test
	void test_findCategoriesForUser() {
		//Given
		given(categoryRepository.findAll()).willReturn(List.of(
			createCategory("채소"),
			createCategory("과일"),
			createCategory("수산")
		));

		//When
		List<CategoryResponse> result = sut.findCategories();

		//Then
		assertThat(result).isEmpty();
		then(categoryRepository).should().findAll();
	}
}
