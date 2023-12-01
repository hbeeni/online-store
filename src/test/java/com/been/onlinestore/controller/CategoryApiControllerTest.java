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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.CategoryResponse;
import com.been.onlinestore.service.response.ProductResponse;

@DisplayName("API 컨트롤러 - 카테고리")
@Import(TestSecurityConfig.class)
@WebMvcTest(CategoryApiController.class)
class CategoryApiControllerTest extends RestDocsSupport {

	@MockBean
	private CategoryService categoryService;
	@MockBean
	private ProductService productService;

	@DisplayName("[API][GET] 카테고리 리스트 조회")
	@Test
	void test_getAllCategories() throws Exception {
		//Given
		CategoryResponse response1 = CategoryResponse.of(
				1L,
				"상의",
				"상의입니다.",
				10
		);
		CategoryResponse response2 = CategoryResponse.of(
				2L,
				"하의",
				"하의입니다.",
				20
		);
		given(categoryService.findCategoriesForUser()).willReturn(List.of(response1, response2));

		//When & Then
		mvc.perform(get("/api/categories"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data[0].id").value(response1.id()))
				.andExpect(jsonPath("$.data[0].name").value(response1.name()))
				.andExpect(jsonPath("$.data[0].productCount").value(response1.productCount()))
				.andDo(document(
						"home/category/getCategories",
						homeApiDescription(TagDescription.CATEGORY, "카테고리 조회"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
										.description(CATEGORY_ID.getDescription()),
								fieldWithPath("data[].name").type(JsonFieldType.STRING)
										.description(CATEGORY_NAME.getDescription()),
								fieldWithPath("data[].description").type(JsonFieldType.STRING)
										.description(CATEGORY_DESCRIPTION.getDescription()),
								fieldWithPath("data[].productCount").type(JsonFieldType.NUMBER)
										.description(CATEGORY_PRODUCT_COUNT.getDescription())
						)
				));
		then(categoryService).should().findCategoriesForUser();
	}

	@DisplayName("[API][GET] 해당 카테고리의 상품 리스트 조회 + 페이징")
	@Test
	void test_getAllProductsInCategory_withPagination() throws Exception {
		//Given
		String sortName = "createdAt";
		String direction = "desc";
		int pageNumber = 0;
		int pageSize = 10;
		Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));

		long categoryId = 1L;
		ProductResponse response1 = ProductResponse.of(
				1L,
				"꽃무늬 셔츠",
				12000,
				"이쁜 꽃무늬 셔츠입니다.",
				3000,
				"-"
		);
		ProductResponse response2 = ProductResponse.of(
				2L,
				"체크 셔츠",
				23000,
				"이쁜 체크 셔츠입니다.",
				0,
				"-"
		);
		List<ProductResponse> content = List.of(response1, response2);
		Page<ProductResponse> page = new PageImpl<>(content, pageable, content.size());

		given(productService.findProductsInCategoryForUser(categoryId, pageable)).willReturn(page);

		//When & Then
		mvc.perform(
						get("/api/categories/{categoryId}", categoryId)
								.queryParam("page", String.valueOf(pageNumber))
								.queryParam("size", String.valueOf(pageSize))
								.queryParam("sort", sortName + "," + direction)
				)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("success"))
				.andExpect(jsonPath("$.data").isArray())
				.andExpect(jsonPath("$.data[0].id").value(response1.id()))
				.andExpect(jsonPath("$.data[0].name").value(response1.name()))
				.andExpect(jsonPath("$.page.number").value(page.getNumber()))
				.andExpect(jsonPath("$.page.size").value(page.getSize()))
				.andExpect(jsonPath("$.page.totalPages").value(page.getTotalPages()))
				.andExpect(jsonPath("$.page.totalElements").value(page.getTotalElements()))
				.andDo(document(
						"home/category/getProductsInCategory",
						homeApiDescription(TagDescription.CATEGORY, "해당 카테고리의 상품 조회"),
						preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()),
						pathParameters(
								parameterWithName("categoryId").description(CATEGORY_ID.getDescription())
						),
						requestParameters(PAGE_REQUEST_PARAM),
						responseFields(
								RestDocsUtils.STATUS,
								fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
										.description(PRODUCT_ID.getDescription()),
								fieldWithPath("data[].name").type(JsonFieldType.STRING)
										.description(PRODUCT_NAME.getDescription()),
								fieldWithPath("data[].price").type(JsonFieldType.NUMBER)
										.description(PRODUCT_PRICE.getDescription()),
								fieldWithPath("data[].description").type(JsonFieldType.STRING)
										.description(PRODUCT_DESCRIPTION.getDescription()),
								fieldWithPath("data[].deliveryFee").type(JsonFieldType.NUMBER)
										.description(PRODUCT_DELIVERY_FEE.getDescription()),
								fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
										.description(PRODUCT_IMAGE_URL.getDescription())
						).and(PAGE_INFO)
				));
	}
}
