package com.been.onlinestore.service.admin;

import static com.been.onlinestore.util.CategoryTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.dto.request.CategoryServiceRequest;
import com.been.onlinestore.service.dto.response.admin.AdminCategoryResponse;

@DisplayName("어드민 비즈니스 로직 - 카테고리")
@ExtendWith(MockitoExtension.class)
class AdminCategoryServiceTest {

	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ProductRepository productRepository;

	@InjectMocks
	private AdminCategoryService sut;

	@DisplayName("모든 카테고리 정보를 반환한다.")
	@Test
	void test_findCategoriesForAdmin() {
		//Given
		given(categoryRepository.findAll()).willReturn(List.of(
			createCategory("채소"),
			createCategory("과일"),
			createCategory("수산")
		));

		//When
		List<AdminCategoryResponse> result = sut.findCategories();

		//Then
		assertThat(result).hasSize(3)
			.first()
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("createdBy")
			.hasFieldOrProperty("modifiedAt")
			.hasFieldOrProperty("modifiedBy");
		then(categoryRepository).should().findAll();
	}

	@DisplayName("카테고리를 조회하면, 카테고리 정보를 반환한다.")
	@Test
	void test_findCategory() {
		//Given
		long id = 1L;
		Category category = createCategory("카테고리");

		given(categoryRepository.findByIdWithAllProducts(id)).willReturn(Optional.of(category));

		//When
		AdminCategoryResponse result = sut.findCategory(id);

		//Then
		assertThat(result)
			.hasFieldOrPropertyWithValue("id", category.getId())
			.hasFieldOrPropertyWithValue("name", category.getName())
			.hasFieldOrProperty("productCount")
			.hasFieldOrProperty("createdAt");
		then(categoryRepository).should().findByIdWithAllProducts(id);
	}

	@DisplayName("카테고리를 추가하면, 저장된 카테고리의 id를 반환한다.")
	@Test
	void test_addCategory() {
		//Given
		String name = "category";

		given(categoryRepository.findByName(name)).willReturn(Optional.empty());
		given(categoryRepository.save(any())).willReturn(createCategory(name));

		//When
		Long result = sut.addCategory(CategoryServiceRequest.Create.of(name, name));

		//Then
		assertThat(result).isNotNull();
		then(categoryRepository).should().findByName(name);
		then(categoryRepository).should().save(any());
	}

	@DisplayName("카테고리를 추가할 때, 동일한 이름의 카테고리가 이미 존재하면, 예외를 던진다.")
	@Test
	void test_addCategory_throwsIllegalArgumentException() {
		//Given
		String name = "category";
		CategoryServiceRequest.Create request = CategoryServiceRequest.Create.of(name, null);

		given(categoryRepository.findByName(name)).willReturn(Optional.of(createCategory(name)));

		//When & Then
		assertThatThrownBy(() -> sut.addCategory(request))
			.isInstanceOf(IllegalArgumentException.class);
		then(categoryRepository).should().findByName(name);
		then(categoryRepository).shouldHaveNoMoreInteractions();
	}

	@DisplayName("카테고리 정보를 수정하면, 수정된 카테고리의 id를 반환한다.")
	@Test
	void test_updateCategory() {
		//Given
		long id = 1L;
		Category category = createCategory(id, "category");
		CategoryServiceRequest.Update serviceRequest =
			CategoryServiceRequest.Update.of("update category", "update cateogory");

		given(categoryRepository.findById(id)).willReturn(Optional.of(category));

		//When
		Long result = sut.updateCategory(id, serviceRequest);

		//Then
		assertThat(result).isEqualTo(id);
		assertThat(category).hasFieldOrPropertyWithValue("name", serviceRequest.name());
		then(categoryRepository).should().findById(id);
	}

	@DisplayName("존재하지 않는 카테고리 정보를 수정하려고 하면, 예외를 던진다.")
	@Test
	void test_updateCategory_withNonexistentId_throwsEntityNotFoundException() {
		//Given
		long id = 1L;
		CategoryServiceRequest.Update serviceRequest =
			CategoryServiceRequest.Update.of("update category", "update category");

		given(categoryRepository.findById(id)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.updateCategory(id, serviceRequest))
			.isInstanceOf(EntityNotFoundException.class);
		then(categoryRepository).should().findById(id);
	}

	@DisplayName("카테고리를 삭제하면, 삭제된 카테고리의 id를 반환한다.")
	@Test
	void test_deleteCategory() {
		//Given
		long id = 1L;

		given(productRepository.bulkCategoryNull(id)).willReturn(0);
		willDoNothing().given(categoryRepository).deleteById(id);

		//When
		Long result = sut.deleteCategory(id);

		//Then
		assertThat(result).isEqualTo(id);
		then(productRepository).should().bulkCategoryNull(id);
		then(categoryRepository).should().deleteById(id);
	}
}
