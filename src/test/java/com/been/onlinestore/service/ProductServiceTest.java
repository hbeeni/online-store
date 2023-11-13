package com.been.onlinestore.service;

import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.ProductDto;
import com.been.onlinestore.dto.ProductSearchCondition;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
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
import static com.been.onlinestore.util.ProductTestDataUtil.createProduct;
import static com.been.onlinestore.util.ProductTestDataUtil.createProductDto;
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

    @DisplayName("[판매 중인 상품] 검색어 없이 검색하면, 상품의 페이지를 반환한다.")
    @Test
    void test_findProductsOnSale() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        given(productRepository.findAllOnSale(pageable)).willReturn(Page.empty());

        //When
        Page<ProductDto> result = sut.findProductsOnSale(null, pageable);

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
        Page<ProductDto> result = sut.findProductsOnSale(name, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().findAllOnSaleByName(name, pageable);
    }

    @DisplayName("[판매 중인 상품] 상품을 조회하면, 상품을 반환한다.")
    @Test
    void test_findProductInfoOnSale() {
        //Given
        long id = 1L;
        given(productRepository.findOnSaleById(id)).willReturn(Optional.of(createProduct(id)));

        //When
        ProductDto result = sut.findProductInfoOnSale(id);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().findOnSaleById(id);
    }

    @DisplayName("[판매 중인 상품] 상품이 없으면, 예외를 던진다.")
    @Test
    void test_findProductInfoOnSale_throwsException() {
        //Given
        long id = 1L;
        given(productRepository.findOnSaleById(id)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findProductInfoOnSale(id))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().findOnSaleById(id);
    }

    @DisplayName("검색어 없이 상품을 검색하면, 상품의 페이지를 반환한다.")
    @Test
    void test_findProducts() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        given(productRepository.searchProducts(null, pageable)).willReturn(Page.empty());

        //When
        Page<ProductDto> result = sut.findProducts(null, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProducts(null, pageable);
    }

    @DisplayName("검색어와 함께 상품을 검색하면, 검색 조건에 맞는 상품의 페이지를 반환한다.")
    @Test
    void test_findProducts_withKeyword() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        ProductSearchCondition searchCondition = ProductSearchCondition.of(null, "product", null);
        given(productRepository.searchProducts(searchCondition, pageable)).willReturn(Page.empty());

        //When
        Page<ProductDto> result = sut.findProducts(searchCondition, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProducts(searchCondition, pageable);
    }

    @DisplayName("상품을 조회하면, 상품을 반환한다.")
    @Test
    void test_findProductInfo() {
        //Given
        long id = 1L;
        given(productRepository.findById(id)).willReturn(Optional.of(createProduct(id)));

        //When
        ProductDto result = sut.findProductInfo(id);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().findById(id);
    }

    @DisplayName("상품이 없으면, 예외를 던진다.")
    @Test
    void test_findProductInfo_throwsException() {
        //Given
        long id = 1L;
        given(productRepository.findById(id)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findProductInfo(id))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().findById(id);
    }

    @DisplayName("해당 판매자가 판매하는 상품을 검색어 없이 검색하면, 상품의 페이지를 반환한다.")
    @Test
    void test_findProductsBySellerId() {
        //Given
        Pageable pageable = PageRequest.of(0, 9, Sort.Direction.DESC, "createdAt");
        long sellerId = 1L;
        given(productRepository.searchProductsBySellerId(sellerId, null, pageable)).willReturn(Page.empty());

        //When
        Page<ProductDto> result = sut.findProductsBySellerId(sellerId, null, pageable);

        //Then
        assertThat(result).isEmpty();
        then(productRepository).should().searchProductsBySellerId(sellerId, null, pageable);
    }

    @DisplayName("해당 판매자가 판매하는 상품을 조회하면, 상품을 반환한다.")
    @Test
    void test_findProductInfoBySellerId() {
        //Given
        long productId = 1L;
        long sellerId = 1L;
        given(productRepository.findByIdAndSeller_Id(productId, sellerId)).willReturn(Optional.of(createProduct(productId)));

        //When
        ProductDto result = sut.findProductInfoBySellerId(productId, sellerId);

        //Then
        assertThat(result).isNotNull();
        then(productRepository).should().findByIdAndSeller_Id(productId, sellerId);
    }

    @DisplayName("해당 판매자가 판매하지 않는 상품을 조회하면, 예외를 반환한다.")
    @Test
    void test_findProductInfoBySellerId_throwsException() {
        //Given
        long productId = 1L;
        long sellerId = 1L;
        given(productRepository.findByIdAndSeller_Id(productId, sellerId)).willReturn(Optional.empty());

        //When & Then
        assertThatThrownBy(() -> sut.findProductInfoBySellerId(productId, sellerId))
                .isInstanceOf(EntityNotFoundException.class);
        then(productRepository).should().findByIdAndSeller_Id(productId, sellerId);
    }

    @DisplayName("상품을 등록하면, 등록된 상품의 id를 반환한다.")
    @Test
    void test_addProduct() {
        //Given
        long productId = 1L;

        ProductDto dto = createProductDto(productId);
        long categoryId = dto.categoryDto().id();
        long userId = dto.sellerDto().id();

        given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));
        given(userRepository.getReferenceById(userId)).willReturn(createUser("user"));
        given(productRepository.save(any())).willReturn(createProduct());

        //When
        Long result = sut.addProduct(categoryId, dto);

        //Then
        assertThat(result).isEqualTo(productId);
        then(categoryRepository).should().getReferenceById(categoryId);
        then(userRepository).should().getReferenceById(userId);
        then(productRepository).should().save(any());
    }

    @DisplayName("상품 정보를 수정하면, 수정된 상품의 id를 반환한다.")
    @Test
    void test_updateProductInfo() {
        //Given
        long productId = 1L;

        ProductDto dto = createProductDto(productId);
        long categoryId = dto.categoryDto().id();

        given(productRepository.getReferenceById(productId)).willReturn(createProduct(productId));
        given(categoryRepository.getReferenceById(categoryId)).willReturn(createCategory("category"));

        //When
        Long result = sut.updateProductInfo(productId, categoryId, dto);

        //Then
        assertThat(result).isEqualTo(productId);
        then(productRepository).should().getReferenceById(categoryId);
    }

    @DisplayName("상품 재고를 변경한다.")
    @Test
    void test_updateProductStockQuantity() {
        //Given
        long id = 1L;
        int stockQuantity = 100;
        given(productRepository.getReferenceById(id)).willReturn(createProduct(id));

        //When
        Long result = sut.updateProductStockQuantity(id, stockQuantity);

        //Then
        assertThat(result).isEqualTo(id);
        then(productRepository).should().getReferenceById(id);
    }

    @DisplayName("상품 판매 상태를 변경한다.")
    @Test
    void test_findProductsWithStatusSaleAndKeyword() {
        //Given
        long id = 1L;
        SaleStatus saleStatus = SaleStatus.CLOSE;
        given(productRepository.getReferenceById(id)).willReturn(createProduct(id));

        //When
        Long result = sut.updateProductSaleStatus(id, saleStatus);

        //Then
        assertThat(result).isEqualTo(id);
        then(productRepository).should().getReferenceById(id);
    }

    @DisplayName("없는 상품의 수정 정보를 입력하면, 경고 로그를 찍고 아무것도 하지 않는다.")
    @Test
    void testUpdateProductInfo_logWarn() {
        //Given
        ProductDto dto = createProductDto(1L);
        given(productRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        //When
        Long result = sut.updateProductInfo(dto.id(), dto.categoryDto().id(), dto);

        //Then
        assertThat(result).isNull();
        then(productRepository).should().getReferenceById(dto.id());
        then(categoryRepository).shouldHaveNoInteractions();
    }
}
