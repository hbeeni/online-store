package com.been.onlinestore.service;

import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.request.ProductServiceRequest;
import com.been.onlinestore.service.response.CategoryProductResponse;
import com.been.onlinestore.service.response.ProductResponse;
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

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static com.been.onlinestore.util.CategoryTestDataUtil.createCategory;
import static com.been.onlinestore.util.ProductTestDataUtil.createAdminProductResponse;
import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@DisplayName("비즈니스 로직 - 상품")
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProductService sut;

    @DisplayName("[판매 중인 상품] 해당 카테고리의 상품의 페이지를 반환한다.")
    @Test
    void test_findProductsInCategory() {
        //Given
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
        given(productRepository.findAllOnSaleByCategory(categoryId, pageable)).willReturn(Page.empty());

        //When
        Page<ProductResponse> products = sut.findProductsInCategoryForUser(categoryId, pageable);

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
        Page<CategoryProductResponse> result = sut.findProductsOnSaleForUser(null, pageable);

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
        Page<CategoryProductResponse> result = sut.findProductsOnSaleForUser(name, pageable);

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

        //When
        CategoryProductResponse result = sut.findProductOnSaleForUser(id);

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
        assertThatThrownBy(() -> sut.findProductOnSaleForUser(id))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().findOnSaleById(id);
    }

    @DisplayName("검색어 없이 상품을 검색하면, 상품의 페이지를 반환한다.")
    @Test
    void test_findProducts() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        given(productRepository.searchProducts(null, null, pageable)).willReturn(Page.empty());

        //When
        Page<AdminProductResponse> result = sut.findProductsForAdmin(null, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProducts(null, null, pageable);
    }

    @DisplayName("검색 조건과 함께 상품을 검색하면, 검색 조건에 맞는 상품의 페이지를 반환한다.")
    @Test
    void test_findProducts_withKeyword() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        ProductSearchCondition searchCondition = ProductSearchCondition.of(null, "product", null);
        given(productRepository.searchProducts(null, searchCondition, pageable)).willReturn(Page.empty());

        //When
        Page<AdminProductResponse> result = sut.findProductsForAdmin(searchCondition, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProducts(null, searchCondition, pageable);
    }

    @DisplayName("상품을 조회하면, 상품을 반환한다.")
    @Test
    void test_findProductInfo() {
        //Given
        long id = 1L;
        given(productRepository.searchProduct(id, null)).willReturn(Optional.of(createAdminProductResponse(id)));

        //When
        AdminProductResponse result = sut.findProductForAdmin(id);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().searchProduct(id, null);
    }

    @DisplayName("상품이 없으면, 예외를 던진다.")
    @Test
    void test_findProductInfo_throwsEntityNotFoundException() {
        //Given
        long id = 1L;
        given(productRepository.searchProduct(id, null)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findProductForAdmin(id))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().searchProduct(id, null);
    }

    @DisplayName("해당 판매자가 판매하는 상품을 검색어 없이 검색하면, 상품의 페이지를 반환한다.")
    @Test
    void test_findProductsBySellerId() {
        //Given
        long sellerId = 1L;
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        given(productRepository.searchProducts(sellerId, null, pageable)).willReturn(Page.empty());

        //When
        Page<AdminProductResponse> result = sut.findProductsForSeller(sellerId, null, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProducts(sellerId, null, pageable);
    }

    @DisplayName("해당 판매자가 판매하는 상품을 조회하면, 상품을 반환한다.")
    @Test
    void test_findProductInfoBySellerId() {
        //Given
        long productId = 1L;
        long sellerId = 1L;
        given(productRepository.searchProduct(productId, sellerId)).willReturn(Optional.of(createAdminProductResponse(productId)));

        //When
        AdminProductResponse result = sut.findProductForSeller(productId, sellerId);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().searchProduct(productId, sellerId);
    }

    @DisplayName("해당 판매자가 판매하지 않는 상품을 조회하면, 예외를 반환한다.")
    @Test
    void test_findProductInfoBySellerId_throwsEntityNotFoundException() {
        //Given
        long productId = 1L;
        long sellerId = 1L;
        given(productRepository.searchProduct(productId, sellerId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findProductForSeller(productId, sellerId))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().searchProduct(productId, sellerId);
    }

    @DisplayName("상품을 등록하면, 등록된 상품의 id를 반환한다.")
    @Test
    void test_addProduct() {
        //Given
        long categoryId = 1L;
        long productId = 1L;
        long sellerId = 1L;
        ProductServiceRequest serviceRequest =
                ProductServiceRequest.of(categoryId, "product", 10000, "des", 100, null, 3000, null);

        given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));
        given(userRepository.getReferenceById(sellerId)).willReturn(createUser("user"));
        given(productRepository.save(any())).willReturn(createProduct());

        //When
        Long result = sut.addProduct(sellerId, serviceRequest);

        //Then
        assertThat(result).isEqualTo(productId);
        then(categoryRepository).should().getReferenceById(categoryId);
        then(userRepository).should().getReferenceById(sellerId);
        then(productRepository).should().save(any());
    }

    @DisplayName("상품 정보를 수정하면, 수정된 상품의 id를 반환한다.")
    @Test
    void test_updateProductInfo() {
        //Given
        long productId = 1L;
        long sellerId = 1L;
        long categoryId = 1L;
        ProductServiceRequest serviceRequest =
                ProductServiceRequest.of(categoryId, "product", 10000, "des", 100, SaleStatus.CLOSE, 3000, null);

        given(productRepository.findByIdAndSeller_Id(productId, sellerId)).willReturn(Optional.of(createProduct(productId)));
        given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));

        //When
        Long result = sut.updateProductInfo(productId, categoryId, serviceRequest);

        //Then
        assertThat(result).isEqualTo(productId);
        then(productRepository).should().findByIdAndSeller_Id(productId, sellerId);
        then(categoryRepository).should().getReferenceById(categoryId);
    }

    @DisplayName("없는 상품의 수정 정보를 입력하면, 예외를 던진다.")
    @Test
    void testUpdateProductInfo_throwsEntityNotFoundException() {
        //Given
        long categoryId = 1L;
        long sellerId = 1L;
        long productId = 1L;
        ProductServiceRequest serviceRequest =
                ProductServiceRequest.of(categoryId, "product", 10000, "des", 100, SaleStatus.CLOSE, 3000, null);

        given(productRepository.findByIdAndSeller_Id(productId, sellerId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.updateProductInfo(productId, categoryId, serviceRequest))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().findByIdAndSeller_Id(productId, sellerId);
        then(categoryRepository).shouldHaveNoInteractions();
    }
}
