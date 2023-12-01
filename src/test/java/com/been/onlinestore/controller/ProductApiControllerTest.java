package com.been.onlinestore.controller;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.CategoryProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.UUID;

import static com.been.onlinestore.controller.restdocs.FieldDescription.CATEGORY_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_DELIVERY_FEE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_DESCRIPTION;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_IMAGE_URL;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.PRODUCT_PRICE;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.PAGE_INFO;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.PAGE_REQUEST_PARAM;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.homeApiDescription;
import static com.been.onlinestore.util.ProductTestDataUtil.createCategoryProductResponse;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 상품")
@Import(TestSecurityConfig.class)
@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest extends RestDocsSupport {

    @Value("${image.path}")
    private String imagePath;

    @MockBean private ProductService productService;
    @MockBean private ImageStore imageStore;

    @DisplayName("[API][GET] 신상품 조회 - 기본: 생성일 내림차순으로 20개")
    @Test
    void test_get20ProductsOrderByCreatedAtDesc() throws Exception {
        //Given
        long productId = 1L;
        String productName = "test product";
        int productPrice = 10000;

        int pageNumber = 0;
        int pageSize = 20;
        String sortName = "createdAt";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        Page<CategoryProductResponse> page = new PageImpl<>(List.of(createCategoryProductResponse(productId, productName, "category")), pageable, 1);

        given(productService.findProductsOnSaleForUser(null, pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                        get("/api/products")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + ",desc")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(productId))
                .andExpect(jsonPath("$.data[0].name").value(productName))
                .andExpect(jsonPath("$.data[0].price").value(productPrice))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
        then(productService).should().findProductsOnSaleForUser(null, pageable);
    }

    @DisplayName("[API][GET] 상품 조회 - 생성일 내림차순으로 20개 + 상품명 검색")
    @Test
    void test_getProducts_whenSearchingProductName() throws Exception {
        //Given
        int pageNumber = 0;
        int pageSize = 20;
        String sortName = "createdAt";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        CategoryProductResponse response = CategoryProductResponse.of(
                1L,
                "상의",
                "꽃무늬 셔츠",
                12000,
                "이쁜 꽃무늬 셔츠입니다.",
                3000,
                imagePath + UUID.randomUUID() + ".png"
        );
        Page<CategoryProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

        given(productService.findProductsOnSaleForUser(response.name(), pageable)).willReturn(page);

        //When & Then
        mvc.perform(
                    get("/api/products")
                            .queryParam("page", String.valueOf(pageNumber))
                            .queryParam("size", String.valueOf(pageSize))
                            .queryParam("sort", sortName + ",desc")
                            .queryParam("searchName", response.name())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(response.id()))
                .andExpect(jsonPath("$.data[0].name").value(response.name()))
                .andExpect(jsonPath("$.data[0].price").value(response.price()))
                .andExpect(jsonPath("$.page.number").value(page.getNumber()))
                .andExpect(jsonPath("$.page.size").value(page.getSize()))
                .andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
                .andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
                .andDo(document(
                        "home/product/getProducts-searchingProductName",
                        homeApiDescription(TagDescription.PRODUCT, "상품 페이징 조회 + 상품명 검색"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(PAGE_REQUEST_PARAM)
                                .and(parameterWithName("searchName").description("검색할 상품명").optional()),
                        responseFields(
                                RestDocsUtils.STATUS,
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description(PRODUCT_ID.getDescription()),
                                fieldWithPath("data[].category").type(JsonFieldType.VARIES).description(CATEGORY_NAME.getDescription()),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description(PRODUCT_NAME.getDescription()),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description(PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data[].description").type(JsonFieldType.VARIES).description(PRODUCT_DESCRIPTION.getDescription()),
                                fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER).description(PRODUCT_DELIVERY_FEE.getDescription()),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description(PRODUCT_IMAGE_URL.getDescription())
                        ).and(PAGE_INFO)
                ));
        then(productService).should().findProductsOnSaleForUser(response.name(), pageable);
    }

    @DisplayName("[API][GET] 상품 상세 조회")
    @Test
    void test_getProduct() throws Exception {
        //Given
        CategoryProductResponse response = CategoryProductResponse.of(
                1L,
                "상의",
                "꽃무늬 셔츠",
                12000,
                "이쁜 꽃무늬 셔츠입니다.",
                3000,
                imagePath + UUID.randomUUID() + ".png"
        );
        given(productService.findProductOnSaleForUser(response.id())).willReturn(response);

        //When & Then
        mvc.perform(get("/api/products/{productId}", response.id()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(response.id()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.price").value(response.price()))
                .andDo(document(
                        "home/product/getProduct",
                        homeApiDescription(TagDescription.PRODUCT, "상품 상세 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("productId").description(PRODUCT_ID.getDescription())
                        ),
                        responseFields(
                                RestDocsUtils.STATUS,
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description(PRODUCT_ID.getDescription()),
                                fieldWithPath("data.category").type(JsonFieldType.VARIES).description(CATEGORY_NAME.getDescription()),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description(PRODUCT_NAME.getDescription()),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description(PRODUCT_PRICE.getDescription()),
                                fieldWithPath("data.description").type(JsonFieldType.VARIES).description(PRODUCT_DESCRIPTION.getDescription()),
                                fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER).description(PRODUCT_DELIVERY_FEE.getDescription()),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description(PRODUCT_IMAGE_URL.getDescription())
                        )
                ));
        then(productService).should().findProductOnSaleForUser(response.id());
    }

    @DisplayName("[API][GET] 상품 이미지 조회")
    @Test
    void test_getProductImage() throws Exception {
        //Given
        String imageName = UUID.randomUUID() + ".png";
        byte[] image = "image".getBytes();

        given(imageStore.getProductImage(imageName)).willReturn(image);

        //When & Then
        mvc.perform(
                        get("/api/products/img/{imageName}", imageName)
                                .accept(MediaType.IMAGE_PNG_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(image))
                .andDo(document(
                        "home/product/getProductImage",
                        homeApiDescription(TagDescription.PRODUCT, "상품 이미지 조회"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("imageName").description("상품 이미지 파일명")
                        ),
                        responseBody()
                ));
        then(imageStore).should().getProductImage(imageName);
    }
}
