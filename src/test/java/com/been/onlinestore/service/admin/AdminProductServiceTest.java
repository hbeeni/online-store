package com.been.onlinestore.service.admin;

import static com.been.onlinestore.util.CategoryTestDataUtil.*;
import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.dto.request.ProductServiceRequest;

@DisplayName("어드민 비즈니스 로직 - 상품")
@ExtendWith(MockitoExtension.class)
class AdminProductServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private CategoryRepository categoryRepository;
	@Mock
	private ImageStore imageStore;

	@InjectMocks
	private AdminProductService sut;

	@DisplayName("검색어 없이 상품을 검색하면, 상품의 페이지를 반환한다.")
	@Test
	void test_findProducts() {
		//Given
		Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");

		given(productRepository.searchProducts(null, pageable)).willReturn(Page.empty());

		//When
		Page<AdminProductResponse> result = sut.findProducts(null, pageable);

		//Then
		assertThat(result).isEmpty();
		then(productRepository).should().searchProducts(null, pageable);
	}

	@DisplayName("검색 조건과 함께 상품을 검색하면, 검색 조건에 맞는 상품의 페이지를 반환한다.")
	@Test
	void test_findProducts_withKeyword() {
		//Given
		Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
		ProductSearchCondition searchCondition = ProductSearchCondition.of(null, "product", null);

		given(productRepository.searchProducts(searchCondition, pageable)).willReturn(Page.empty());

		//When
		Page<AdminProductResponse> result = sut.findProducts(searchCondition, pageable);

		//Then
		assertThat(result).isEmpty();
		then(productRepository).should().searchProducts(searchCondition, pageable);
	}

	@DisplayName("상품을 조회하면, 상품을 반환한다.")
	@Test
	void test_findProductInfo() {
		//Given
		long id = 1L;

		given(productRepository.searchProduct(id)).willReturn(Optional.of(createAdminProductResponse(id)));

		//When
		AdminProductResponse result = sut.findProduct(id);

		//Then
		assertThat(result).isNotNull();
		then(productRepository).should().searchProduct(id);
	}

	@DisplayName("상품이 없으면, 예외를 던진다.")
	@Test
	void test_findProductInfo_throwsCustomException() {
		//Given
		long id = 1L;

		given(productRepository.searchProduct(id)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findProduct(id))
			.isInstanceOf(CustomException.class);
		then(productRepository).should().searchProduct(id);
	}

	@DisplayName("상품을 등록하면, 등록된 상품의 id를 반환한다.")
	@Test
	void test_addProduct() {
		//Given
		long categoryId = 1L;
		long productId = 1L;
		ProductServiceRequest.Create serviceRequest =
			ProductServiceRequest.Create.of(categoryId, "product", 10000, "des", 100, null, 3000);

		given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));
		given(productRepository.save(any())).willReturn(createProduct());

		//When
		Long result = sut.addProduct(serviceRequest, "image.png");

		//Then
		assertThat(result).isEqualTo(productId);
		then(categoryRepository).should().getReferenceById(categoryId);
		then(productRepository).should().save(any());
	}

	@DisplayName("상품 정보를 수정하면, 수정된 상품의 id를 반환한다.")
	@Test
	void test_updateProductInfo() {
		//Given
		long productId = 1L;
		long categoryId = 1L;
		ProductServiceRequest.Update serviceRequest =
			ProductServiceRequest.Update.of(categoryId, "product", 10000, "des", 100, SaleStatus.CLOSE, 3000);

		given(productRepository.findById(productId)).willReturn(Optional.of(createProduct(productId)));
		given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));

		//When
		Long result = sut.updateProductInfo(productId, serviceRequest);

		//Then
		assertThat(result).isEqualTo(productId);
		then(productRepository).should().findById(productId);
		then(categoryRepository).should().getReferenceById(categoryId);
	}

	@DisplayName("없는 상품의 수정 정보를 입력하면, 예외를 던진다.")
	@Test
	void testUpdateProductInfo_throwsCustomException() {
		//Given
		long categoryId = 1L;
		long productId = 1L;
		ProductServiceRequest.Update serviceRequest =
			ProductServiceRequest.Update.of(categoryId, "product", 10000, "des", 100, SaleStatus.CLOSE, 3000);

		given(productRepository.findById(productId)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.updateProductInfo(productId, serviceRequest))
			.isInstanceOf(CustomException.class);
		then(productRepository).should().findById(productId);
		then(categoryRepository).shouldHaveNoInteractions();
	}

	@DisplayName("상품 이미지를 수정하면, 수정된 상품의 id를 반환한다.")
	@Test
	void test_updateProductImage() {
		//Given
		long productId = 1L;

		given(productRepository.findById(productId)).willReturn(Optional.of(createProduct(productId)));
		willDoNothing().given(imageStore).deleteImage(anyString());

		//When
		Long result = sut.updateProductImage(productId, "image.png");

		//Then
		assertThat(result).isEqualTo(productId);
		then(productRepository).should().findById(productId);
		then(imageStore).should().deleteImage(anyString());
	}
}
