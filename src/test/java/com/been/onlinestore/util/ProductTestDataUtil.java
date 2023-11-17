package com.been.onlinestore.util;

import com.been.onlinestore.domain.Product;
import com.been.onlinestore.domain.constant.SaleStatus;
import com.been.onlinestore.dto.ProductDto;
import com.been.onlinestore.dto.UserDto;
import com.been.onlinestore.service.response.ProductResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static com.been.onlinestore.util.CategoryTestDataUtil.createCategory;
import static com.been.onlinestore.util.UserTestDataUtil.createUser;
import static java.time.LocalDateTime.now;

public class ProductTestDataUtil {

    public static Product createProduct() {
        return createProduct(1L);
    }

    public static Product createProduct(Long id) {
        Product product = Product.of(
                createCategory(1L, "category"),
                createUser(),
                "name",
                10000,
                "description",
                1000,
                0,
                SaleStatus.SALE,
                3000,
                null
        );
        ReflectionTestUtils.setField(product, "id", id);
        return product;
    }

    public static ProductDto createProductDto(Long id) {
        return ProductDto.of(
                id,
                null,
                UserDto.from(createUser("uid")),
                "product",
                10000,
                "description",
                1000,
                0,
                SaleStatus.SALE,
                3000,
                null,
                now(),
                "been",
                now(),
                "been"
        );
    }

    public static ProductResponse createProductResponse(Long id) {
        return ProductResponse.of(
                id,
                "name",
                10000,
                "description",
                3000,
                "-"
        );
    }
}
