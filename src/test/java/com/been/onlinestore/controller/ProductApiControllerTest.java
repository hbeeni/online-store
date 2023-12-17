package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

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

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.file.ImageStore;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.dto.response.CategoryProductResponse;

@DisplayName("API 컨트롤러 - 상품")
@Import(TestSecurityConfig.class)
@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest extends RestDocsSupport {

	@Value("${image.path}")
	private String imagePath;

	@MockBean
	private ProductService productService;
	@MockBean
	private ImageStore imageStore;

	@DisplayName("[API][GET] 상품 조회")
	@Test
	void test_getProducts() throws Exception {
		//Given
		int pageNumber = 0;
		int pageSize = 20;
		String sortName = "createdAt";

		CategoryProductResponse response = CategoryProductResponse.of(
			1L,
			"채소",
			"깐대파 500g",
			4500,
			"시원한 국물 맛의 비밀",
			3000,
			imagePath + "c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg"
		);

		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
		Page<CategoryProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

		given(productService.findProductsOnSale(null, pageable)).willReturn(page);

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
			.andExpect(jsonPath("$.data[0].id").value(response.id()))
			.andExpect(jsonPath("$.data[0].name").value(response.name()))
			.andExpect(jsonPath("$.data[0].price").value(response.price()))
			.andExpect(jsonPath("$.page.number").value(page.getNumber()))
			.andExpect(jsonPath("$.page.size").value(page.getSize()))
			.andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
			.andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()));
		then(productService).should().findProductsOnSale(null, pageable);
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
			"채소",
			"깐대파 500g",
			4500,
			"시원한 국물 맛의 비밀",
			3000,
			imagePath + "c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg"
		);
		Page<CategoryProductResponse> page = new PageImpl<>(List.of(response), pageable, 1);

		given(productService.findProductsOnSale(response.name(), pageable)).willReturn(page);

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
					fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data[].category").type(JsonFieldType.VARIES)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("data[].name").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data[].price").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data[].description").type(JsonFieldType.VARIES)
						.description(PRODUCT_DESCRIPTION.getDescription()),
					fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER)
						.description(PRODUCT_DELIVERY_FEE.getDescription()),
					fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
						.description(PRODUCT_IMAGE_URL.getDescription())
				).and(PAGE_INFO)
			));
		then(productService).should().findProductsOnSale(response.name(), pageable);
	}

	@DisplayName("[API][GET] 상품 상세 조회")
	@Test
	void test_getProduct() throws Exception {
		//Given
		CategoryProductResponse response = CategoryProductResponse.of(
			1L,
			"채소",
			"깐대파 500g",
			4500,
			"시원한 국물 맛의 비밀",
			3000,
			imagePath + "c1b2f2a2-f0b8-403a-b03b-351d1ee0bd05.jpg"
		);
		given(productService.findProductOnSale(response.id())).willReturn(response);

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
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(PRODUCT_ID.getDescription()),
					fieldWithPath("data.category").type(JsonFieldType.VARIES)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("data.name").type(JsonFieldType.STRING)
						.description(PRODUCT_NAME.getDescription()),
					fieldWithPath("data.price").type(JsonFieldType.NUMBER)
						.description(PRODUCT_PRICE.getDescription()),
					fieldWithPath("data.description").type(JsonFieldType.VARIES)
						.description(PRODUCT_DESCRIPTION.getDescription()),
					fieldWithPath("data.deliveryFee").type(JsonFieldType.NUMBER)
						.description(PRODUCT_DELIVERY_FEE.getDescription()),
					fieldWithPath("data.imageUrl").type(JsonFieldType.STRING)
						.description(PRODUCT_IMAGE_URL.getDescription())
				)
			));
		then(productService).should().findProductOnSale(response.id());
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
