package com.been.onlinestore.service;

import static org.springframework.util.StringUtils.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.enums.ErrorMessages;
import com.been.onlinestore.exception.CustomException;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.dto.response.CategoryProductResponse;
import com.been.onlinestore.service.dto.response.ProductResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final ImageStore imageStore;

	public Page<ProductResponse> findProductsInCategory(Long categoryId, Pageable pageable) {
		return productRepository.findAllOnSaleByCategory(categoryId, pageable)
			.map(product -> ProductResponse.from(product, imageStore.getImageUrl(product.getImageName())));
	}

	public Page<CategoryProductResponse> findProductsOnSale(String name, Pageable pageable) {
		if (hasText(name)) {
			return productRepository.findAllOnSaleByName(name, pageable)
				.map(product -> CategoryProductResponse.from(product, imageStore.getImageUrl(product.getImageName())));
		} else {
			return productRepository.findAllOnSale(pageable)
				.map(product -> CategoryProductResponse.from(product, imageStore.getImageUrl(product.getImageName())));
		}
	}

	public CategoryProductResponse findProductOnSale(Long id) {
		return productRepository.findOnSaleById(id)
			.map(product -> CategoryProductResponse.from(product, imageStore.getImageUrl(product.getImageName())))
			.orElseThrow(() -> new CustomException(ErrorMessages.NOT_FOUND_PRODUCT));
	}
}
