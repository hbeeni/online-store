package com.been.onlinestore.service;

import static org.springframework.util.StringUtils.*;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.User;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.UserRepository;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.request.ProductServiceRequest;
import com.been.onlinestore.service.response.CartResponse;
import com.been.onlinestore.service.response.CategoryProductResponse;
import com.been.onlinestore.service.response.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final ImageStore imageStore;

	@Transactional(readOnly = true)
	public Page<ProductResponse> findProductsInCategoryForUser(Long categoryId, Pageable pageable) {
		return productRepository.findAllOnSaleByCategory(categoryId, pageable)
				.map(product -> ProductResponse.from(product, imageStore.getImageUrl(product.getImageName())));
	}

	@Transactional(readOnly = true)
	public Page<CategoryProductResponse> findProductsOnSaleForUser(String name, Pageable pageable) {
		if (hasText(name)) {
			return productRepository.findAllOnSaleByName(name, pageable)
					.map(product -> CategoryProductResponse.from(product,
							imageStore.getImageUrl(product.getImageName())));
		} else {
			return productRepository.findAllOnSale(pageable)
					.map(product -> CategoryProductResponse.from(product,
							imageStore.getImageUrl(product.getImageName())));
		}
	}

	@Transactional(readOnly = true)
	public CategoryProductResponse findProductOnSaleForUser(Long id) {
		return productRepository.findOnSaleById(id)
				.map(product -> CategoryProductResponse.from(product, imageStore.getImageUrl(product.getImageName())))
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
	}

	public List<CartResponse> findProductsInCart(Map<Long, Integer> productToQuantityMap) {
		return productRepository.findAllOnSaleById(productToQuantityMap.keySet()).stream()
				.map(product -> CartResponse.from(product, productToQuantityMap))
				.toList();
	}

	@Transactional(readOnly = true)
	public Page<AdminProductResponse> findProductsForAdmin(ProductSearchCondition cond, Pageable pageable) {
		return productRepository.searchProducts(null, cond, pageable);
	}

	@Transactional(readOnly = true)
	public Page<AdminProductResponse> findProductsForSeller(Long sellerId, ProductSearchCondition cond,
			Pageable pageable) {
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

	public Long addProduct(Long sellerId, ProductServiceRequest.Create serviceRequest, String imageName) {
		Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
		User user = userRepository.getReferenceById(sellerId);
		if (serviceRequest.saleStatus() == null) {
			return productRepository.save(serviceRequest.toEntity(category, user, SaleStatus.WAIT, imageName)).getId();
		}
		return productRepository.save(serviceRequest.toEntity(category, user, imageName)).getId();
	}

	public Long updateProductInfo(Long productId, Long sellerId, ProductServiceRequest.Update serviceRequest) {
		Product product = productRepository.findByIdAndSeller_Id(productId, sellerId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
		Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
		product.updateInfo(category, serviceRequest.name(), serviceRequest.price(), serviceRequest.description(),
				serviceRequest.stockQuantity(), serviceRequest.saleStatus(), serviceRequest.deliveryFee());
		return product.getId();
	}

	public Long updateProductImage(Long productId, Long sellerId, String imageName) {
		Product product = productRepository.findByIdAndSeller_Id(productId, sellerId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
		imageStore.deleteImage(product.getImageName());
		product.updateImage(imageName);
		return product.getId();
	}
}
