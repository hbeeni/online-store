package com.been.onlinestore.controller.admin;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.response.CategoryResponse;
import com.been.onlinestore.dto.CategoryDto;
import com.been.onlinestore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/admin/categories")
@RestController
public class AdminCategoryApiController {

    private final CategoryService categoryService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable Long categoryId) {
        CategoryResponse response = CategoryResponse.from(categoryService.findCategory(categoryId));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> addCategory(@RequestBody CategoryRequest request) {
        Long id = categoryService.addCategory(request.toDto());
        return ResponseEntity.ok(ApiResponse.successWithId(id));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest request) {
        Long id = categoryService.updateCategory(categoryId, request.toDto());
        return ResponseEntity.ok(ApiResponse.successWithId(id));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> deleteCategory(@PathVariable Long categoryId) {
        Long id = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.successWithId(id));
    }

    public record CategoryRequest(String name, String description) {
        public CategoryDto toDto() {
            return CategoryDto.of(name, description);
        }
    }
}
