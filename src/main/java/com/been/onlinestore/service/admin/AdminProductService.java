package com.been.onlinestore.service.admin;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.repository.querydsl.product.AdminProductResponse;
import com.been.onlinestore.repository.querydsl.product.ProductSearchCondition;
import com.been.onlinestore.service.dto.request.ProductServiceRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;
	private final ImageStore imageStore;

	@Transactional(readOnly = true)
	public Page<AdminProductResponse> findProducts(ProductSearchCondition cond, Pageable pageable) {
		return productRepository.searchProducts(cond, pageable);
	}

	@Transactional(readOnly = true)
	public AdminProductResponse findProduct(Long id) {
		return productRepository.searchProduct(id)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_PRODUCT.getMessage()));
	}

	public Long addProduct(ProductServiceRequest.Create serviceRequest, String imageName) {
		Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
		if (serviceRequest.saleStatus() == null) {
			return productRepository.save(serviceRequest.toEntity(category, SaleStatus.WAIT, imageName)).getId();
		}
		return productRepository.save(serviceRequest.toEntity(category, imageName)).getId();
	}

	public Long updateProductInfo(Long productId, ProductServiceRequest.Update serviceRequest) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
		Category category = categoryRepository.getReferenceById(serviceRequest.categoryId());
		product.updateInfo(
			category, serviceRequest.name(), serviceRequest.price(), serviceRequest.description(),
			serviceRequest.stockQuantity(), serviceRequest.saleStatus(), serviceRequest.deliveryFee()
		);
		return product.getId();
	}

	public Long updateProductImage(Long productId, String imageName) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.FAIL_TO_UPDATE_PRODUCT.getMessage()));
		imageStore.deleteImage(product.getImageName());
		product.updateImage(imageName);
		return product.getId();
	}
}
