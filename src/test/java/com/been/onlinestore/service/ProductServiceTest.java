package com.been.onlinestore.service;

import static com.been.onlinestore.util.ProductTestDataUtil.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

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

import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.dto.response.CartResponse;
import com.been.onlinestore.service.dto.response.CategoryProductResponse;
import com.been.onlinestore.service.dto.response.ProductResponse;

@DisplayName("비즈니스 로직 - 상품")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

	@Mock
	private ProductRepository productRepository;
	@Mock
	private ImageStore imageStore;

	@InjectMocks
	private ProductService sut;

	@DisplayName("[판매 중인 상품] 해당 카테고리의 상품의 페이지를 반환한다.")
	@Test
	void test_findProductsInCategory() {
		//Given
		Long categoryId = 1L;
		Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
		given(productRepository.findAllOnSaleByCategory(categoryId, pageable)).willReturn(Page.empty());

		//When
		Page<ProductResponse> products = sut.findProductsInCategory(categoryId, pageable);

		//Then
		assertThat(products).isEmpty();
		then(productRepository).should().findAllOnSaleByCategory(categoryId, pageable);
	}

	@DisplayName("[판매 중인 상품] 검색어 없이 검색하면, 상품의 페이지를 반환한다.")
	@Test
	void test_findProductsOnSale() {
		//Given
		Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
		given(productRepository.findAllOnSale(pageable)).willReturn(Page.empty());

		//When
		Page<CategoryProductResponse> result = sut.findProductsOnSale(null, pageable);

		//Then
		assertThat(result).isEmpty();
		then(productRepository).should().findAllOnSale(pageable);
	}

	@DisplayName("[판매 중인 상품] 검색어와 함께 검색하면, 상품명에 검색어가 포함된 상품의 페이지를 반환한다.")
	@Test
	void test_findProductsOnSale_withKeyword() {
		//Given
		Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
		String name = "keyword";
		given(productRepository.findAllOnSaleByName(name, pageable)).willReturn(Page.empty());

		//When
		Page<CategoryProductResponse> result = sut.findProductsOnSale(name, pageable);

		//Then
		assertThat(result).isEmpty();
		then(productRepository).should().findAllOnSaleByName(name, pageable);
	}

	@DisplayName("[판매 중인 상품] 상품을 조회하면, 상품을 반환한다.")
	@Test
	void test_findProductOnSale() {
		//Given
		long id = 1L;
		given(productRepository.findOnSaleById(id)).willReturn(Optional.of(createProduct(id)));
		given(imageStore.getImageUrl(anyString())).willReturn("image url");

		//When
		CategoryProductResponse result = sut.findProductOnSale(id);

		//Then
		assertThat(result).isNotNull();
		then(productRepository).should().findOnSaleById(id);
	}

	@DisplayName("[판매 중인 상품] 상품이 없으면, 예외를 던진다.")
	@Test
	void test_findProductOnSale_throwsEntityNotFoundException() {
		//Given
		long id = 1L;
		given(productRepository.findOnSaleById(id)).willReturn(Optional.empty());

		//When & Then
		assertThatThrownBy(() -> sut.findProductOnSale(id))
			.isInstanceOf(EntityNotFoundException.class);
		then(productRepository).should().findOnSaleById(id);
	}

	@DisplayName("장바구니에 있는 상품의 id와 수량으로 조회하면, 상품을 반환한다.")
	@Test
	void test_findProductsInCart() {
		//Given
		long productId = 1L;
		Map<Long, Integer> productIdToQuantityMap = Map.of(productId, 1);

		given(productRepository.findAllOnSaleById(productIdToQuantityMap.keySet()))
			.willReturn(List.of(createProduct(productId)));

		//When
		CartResponse result = sut.findProductsInCart(productIdToQuantityMap);

		//Then
		assertThat(result).isNotNull();
		then(productRepository).should().findAllOnSaleById(productIdToQuantityMap.keySet());
	}
}
