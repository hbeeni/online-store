package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.request.ProductServiceRequest;
import com.been.onlinestore.service.response.CategoryProductResponse;
import com.been.onlinestore.service.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

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
    public Page<ProductResponse> findProductsInCategoryForUser(Long categoryId, Pageable pageable) {
        return productRepository.findAllOnSaleByCategory(categoryId, pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<CategoryProductResponse> findProductsOnSaleForUser(String name, Pageable pageable) {
        if (hasText(name)) {
            return productRepository.findAllOnSaleByName(name, pageable).map(CategoryProductResponse::from);
        } else {
            return productRepository.findAllOnSale(pageable).map(CategoryProductResponse::from);
        }
    }

    @Transactional(readOnly = true)
    public CategoryProductResponse findProductOnSaleForUser(Long id) {
        return productRepository.findOnSaleById(id)
                .map(CategoryProductResponse::from)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> findProductsForAdmin(ProductSearchCondition cond, Pageable pageable) {
        return productRepository.searchProducts(null, cond, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AdminProductResponse> findProductsForSeller(Long sellerId, ProductSearchCondition cond, Pageable pageable) {
        return productRepository.searchProducts(sellerId, cond, pageable);
    }

    @Transactional(readOnly = true)
    public AdminProductResponse findProductForAdmin(Long id) {
        return productRepository.searchProduct(id, null)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
    }

    @Transactional(readOnly = true)
    public AdminProductResponse findProductForSeller(Long productId, Long sellerId) {
        return productRepository.searchProduct(productId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
    }

    public Long addProduct(Long sellerId, ProductServiceRequest serviceRequest) {
        Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
        User user = userRepository.getReferenceById(sellerId);
        return productRepository.save(serviceRequest.toEntity(category, user)).getId();
    }

    public Long updateProductInfo(Long productId, Long sellerId, ProductServiceRequest serviceRequest) {
        Product product = productRepository.findByIdAndSeller_Id(productId, sellerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
        Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
        product.updateInfo(category, serviceRequest.name(), serviceRequest.price(), serviceRequest.description(), serviceRequest.stockQuantity(), serviceRequest.saleStatus(), serviceRequest.deliveryFee(), serviceRequest.imageUrl());
        return product.getId();
    }
}
