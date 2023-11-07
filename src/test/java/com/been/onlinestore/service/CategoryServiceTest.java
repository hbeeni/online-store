package com.been.onlinestore.service;

import com.been.onlinestore.domain.Category;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.CategoryDto;
import com.been.onlinestore.repository.CategoryRepository;
import com.been.onlinestore.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.been.onlinestore.util.CategoryTestDataUtil.createCategory;
import static com.been.onlinestore.util.CategoryTestDataUtil.createCategoryDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

@DisplayName("비즈니스 로직 - 카테고리")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private CategoryService sut;

    @DisplayName("모든 카테고리 정보를 반환한다.")
    @Test
    void test_searchCategories() {
        //Given
        given(categoryRepository.findAll()).willReturn(List.of(
                createCategory("상의"),
                createCategory("하의"),
                createCategory("음식")
        ));

        //When
        List<CategoryDto> result = sut.findCategories();

        //Then
        assertThat(result.size()).isEqualTo(3);
        then(categoryRepository).should().findAll();
    }

    @DisplayName("카테고리를 조회하면, 카테고리 정보를 반환한다.")
    @Test
    void test_searchCategory() {
        //Given
        long id = 1L;
        int productCount = 5;
        Category category = createCategory("카테고리");
        given(categoryRepository.findById(id)).willReturn(Optional.of(category));
        given(productRepository.countByCategory_IdAndSaleStatusEquals(id, SaleStatus.SALE)).willReturn(productCount);

        //When
        CategoryDto result = sut.findCategory(id);

        //Then
        assertThat(result)
                .hasFieldOrPropertyWithValue("id", category.getId())
                .hasFieldOrPropertyWithValue("name", category.getName())
                .hasFieldOrPropertyWithValue("productCount", productCount);
        then(categoryRepository).should().findById(id);
    }

    @DisplayName("카테고리를 추가하면, 저장된 카테고리의 id를 반환한다.")
    @Test
    void test_addCategory() {
        //Given
        String name = "category";
        given(categoryRepository.save(any())).willReturn(createCategory(name));

        //When
        Long result = sut.addCategory(createCategoryDto(name));

        //Then
        assertThat(result).isEqualTo(1L);
        then(categoryRepository).should().save(any());
    }

    @DisplayName("카테고리 정보를 수정하면, 수정된 카테고리의 id를 반환한다.")
    @Test
    void test_updateCategory() {
        //Given
        long id = 1L;
        Category category = createCategory(id, "category");
        CategoryDto dto = createCategoryDto(id, "update category");
        given(categoryRepository.getReferenceById(id)).willReturn(category);

        //When
        Long result = sut.updateCategory(id, dto);

        //Then
        assertThat(result).isEqualTo(id);
        assertThat(category).hasFieldOrPropertyWithValue("name", dto.name());
        then(categoryRepository).should().getReferenceById(id);
    }

    @DisplayName("카테고리를 삭제하면, 삭제된 카테고리의 id를 반환한다.")
    @Test
    void test_deleteCategory() {
        //Given
        long id = 1L;
        willDoNothing().given(categoryRepository).deleteById(id);

        //When
        Long result = sut.deleteCategory(id);

        //Then
        assertThat(result).isEqualTo(id);
        then(categoryRepository).should().deleteById(id);
    }
}
