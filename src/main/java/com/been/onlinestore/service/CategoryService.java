package com.been.onlinestore.service;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.constant.SaleStatus;
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
        return categoryRepository.findAll().stream()
                .map(category -> CategoryDto.of(category, getProductForSaleCount(category.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(category -> CategoryDto.of(category, getProductForSaleCount(categoryId)))
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리가 없습니다 - categoryId: " + categoryId));
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
            log.warn("카테고리 수정 실패. 카테고리를 수정하는데 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }

        return null;
    }

    public Long deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        return categoryId;
    }

    private int getProductForSaleCount(Long categoryId) {
        return productRepository.countByCategory_IdAndSaleStatusEquals(categoryId, SaleStatus.SALE);
    }
}
