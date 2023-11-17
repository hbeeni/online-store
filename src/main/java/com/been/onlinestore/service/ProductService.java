package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.ProductDto;
import com.been.onlinestore.dto.ProductSearchCondition;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.service.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductsInCategory(Long categoryId, Pageable pageable) {
        return productRepository.findAllByCategory_Id(categoryId, pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> findProductsOnSale(String name, Pageable pageable) {
        Page<Product> products;
        if (hasText(name)) {
            products = productRepository.findAllOnSaleByName(name, pageable);
        } else {
            products = productRepository.findAllOnSale(pageable);
        }
        return products.map(ProductDto::from);
    }

    @Transactional(readOnly = true)
    public ProductDto findProductInfoOnSale(Long id) {
        return productRepository.findOnSaleById(id)
                .map(ProductDto::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_SALE_PRODUCT.getMessage()));
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> findProducts(ProductSearchCondition cond, Pageable pageable) {
        return productRepository.searchProducts(cond, pageable)
                .map(ProductDto::from);
    }

    @Transactional(readOnly = true)
    public ProductDto findProductInfo(Long id) {
        return productRepository.findById(id)
                .map(ProductDto::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
    }

    @Transactional(readOnly = true)
    public Page<ProductDto> findProductsBySellerId(Long sellerId, ProductSearchCondition cond, Pageable pageable) {
        return productRepository.searchProductsBySellerId(sellerId, cond, pageable)
                .map(ProductDto::from);
    }

    @Transactional(readOnly = true)
    public ProductDto findProductInfoBySellerId(Long productId, Long sellerId) {
        return productRepository.findByIdAndSeller_Id(productId, sellerId)
                .map(ProductDto::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_SALE_PRODUCT.getMessage()));
    }

    public Long addProduct(Long categoryId, ProductDto dto) {
        Category category = categoryRepository.getReferenceById(categoryId);
        User user = userRepository.getReferenceById(dto.sellerDto().id());
        return productRepository.save(dto.toEntity(category, user)).getId();
    }

    public Long updateProductInfo(Long productId, Long categoryId, ProductDto dto) {
        Product product = getProduct(productId);
        product.updateInfo(categoryRepository.getReferenceById(categoryId), dto.name(), dto.price(), dto.description(), dto.imageUrl());
        return product.getId();
    }

    public Long updateProductStockQuantity(Long id, Integer stockQuantity) {
        Product product = getProduct(id);
        product.updateStockQuantity(stockQuantity);
        return product.getId();
    }

    public Long updateProductSaleStatus(Long id, SaleStatus saleStatus) {
        Product product = getProduct(id);
        product.updateSaleStatus(saleStatus);
        return product.getId();
    }

    public Long updateDeliveryFee(Long id, Integer deliveryFee) {
        Product product = getProduct(id);
        product.updateDeliveryFee(deliveryFee);
        return product.getId();
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
    }
}
