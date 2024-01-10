package com.been.onlinestore.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.service.dto.response.CategoryResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

	private final CategoryRepository categoryRepository;

	@Transactional(readOnly = true)
	public List<CategoryResponse> findCategories() {
		return categoryRepository.findAllBySellingProducts().stream()
			.map(CategoryResponse::from)
			.toList();
	}
}
