package com.been.onlinestore.service;

import com.been.onlinestore.common.ErrorMessages;
import com.been.onlinestore.domain.Category;
import com.been.onlinestore.dto.CategoryDto;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findCategories() {
        return categoryRepository.findAllWithProducts().stream()
                .map(CategoryDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto findCategory(Long categoryId) {
        return categoryRepository.findWithProductsById(categoryId)
                .map(CategoryDto::from)
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessages.NOT_FOUND_CATEGORY.getMessage()));
    }

    public Long addCategory(CategoryDto dto) {
        Category category = dto.toEntity();
        return categoryRepository.save(category).getId();
    }

    public Long updateCategory(Long categoryId, CategoryDto dto) {
        try {
            Category category = categoryRepository.getReferenceById(categoryId);
            String name = category.getName();
            String description = category.getDescription();
            if (StringUtils.hasText(dto.name())) {
                name = dto.name();
            }
            if (StringUtils.hasText(dto.description())) {
                description = dto.description();
            }
            category.updateCategory(name, description);
            return category.getId();
        } catch (EntityNotFoundException e) {
            log.warn(ErrorMessages.FAIL_TO_UPDATE_CATEGORY.getMessage());
        }

        return null;
    }

    public Long deleteCategory(Long categoryId) {
        productRepository.bulkCategoryNull(categoryId);
        categoryRepository.deleteById(categoryId);
        return categoryId;
    }
}
