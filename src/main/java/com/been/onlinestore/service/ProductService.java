package com.been.onlinestore.service;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.ProductDto;
import com.been.onlinestore.dto.ProductSearchCondition;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
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
                .orElseThrow(() -> new EntityNotFoundException("판매 중인 상품 목록에 없습니다. 상품 ID: " + id));
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
                .orElseThrow(() -> new EntityNotFoundException("상품이 없습니다. 상품 ID: " + id));
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
                .orElseThrow(() -> new EntityNotFoundException("판매자의 상품 목록에 없습니다. 상품 ID: " + productId));
    }

    public Long addProduct(Long categoryId, ProductDto dto) {
        Category category = categoryRepository.getReferenceById(categoryId);
        User user = userRepository.getReferenceById(dto.sellerDto().id());
        return productRepository.save(dto.toEntity(category, user)).getId();
    }

    public Long updateProductInfo(Long productId, Long categoryId, ProductDto dto) {
        return updateProduct(productId, categoryId, dto, null, null);
    }

    public Long updateProductStockQuantity(Long id, Integer stockQuantity) {
        return updateProduct(id, null, null, stockQuantity, null);
    }

    public Long updateProductSaleStatus(Long id, SaleStatus saleStatus) {
        return updateProduct(id, null, null, null, saleStatus);
    }

    private Long updateProduct(Long productId, Long categoryId, ProductDto dto, Integer stockQuantity, SaleStatus saleStatus) {
        try {
            Product product = productRepository.getReferenceById(productId);

            if (dto != null) {
                Category category = product.getCategory();
                String name = product.getName();
                int price = product.getPrice();
                String description = product.getDescription();
                String imageUrl = product.getImageUrl();

                if (categoryId != null) {
                    category = categoryRepository.getReferenceById(categoryId);
                }
                if (hasText(dto.name())) {
                    name = dto.name();
                }
                if (dto.price() != null) {
                    price = dto.price();
                }
                if (hasText(dto.description())) {
                    description = dto.description();
                }
                if (hasText(dto.imageUrl())) {
                    imageUrl = dto.imageUrl();
                }

                product.updateInfo(category, name, price, description, imageUrl);
            } else if (stockQuantity != null) {
                product.updateStockQuantity(stockQuantity);
            } else if (saleStatus != null) {
                product.updateSaleStatus(saleStatus);
            }

            return productId;
        } catch (EntityNotFoundException e) {
            log.warn("상품 업데이트 실패. 상품을 수정하는데 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
            return null;
        }
    }
}
