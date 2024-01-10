package com.been.onlinestore.service.admin;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import com.been.onlinestore.service.dto.request.CategoryServiceRequest;
import com.been.onlinestore.service.dto.response.admin.AdminCategoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AdminCategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<AdminCategoryResponse> findCategories() {
		return categoryRepository.findAll().stream()
			.map(AdminCategoryResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public AdminCategoryResponse findCategory(Long categoryId) {
		return categoryRepository.findByIdWithAllProducts(categoryId)
			.map(AdminCategoryResponse::from)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CATEGORY.getMessage()));
	}

	public Long addCategory(CategoryServiceRequest.Create serviceRequest) {
		Optional<Category> sameCategory = categoryRepository.findByName(serviceRequest.name());
		if (sameCategory.isPresent()) {
			throw new IllegalArgumentException(ErrorMessages.ALREADY_EXISTING_CATEGORY.getMessage());
		}

		Category category = serviceRequest.toEntity();
		return categoryRepository.save(category).getId();
	}

	public Long updateCategory(Long categoryId, CategoryServiceRequest.Update serviceRequest) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorMessages.NOT_FOUND_CATEGORY.getMessage()));

		category.updateCategory(serviceRequest.name(), serviceRequest.description());
		return category.getId();
	}

	public Long deleteCategory(Long categoryId) {
		productRepository.bulkCategoryNull(categoryId);
		categoryRepository.deleteById(categoryId);
		return categoryId;
	}
}
