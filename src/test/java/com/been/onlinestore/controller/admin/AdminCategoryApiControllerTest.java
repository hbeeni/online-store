package com.been.onlinestore.controller.admin;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static java.time.LocalDateTime.*;
import static org.mockito.ArgumentMatchers.*;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.CategoryRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.RestDocsUtils;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.service.CategoryService;
import com.been.onlinestore.service.dto.response.admin.AdminCategoryResponse;

@DisplayName("API 컨트롤러 - 카테고리 (관리자)")
@Import(TestSecurityConfig.class)
@WebMvcTest(AdminCategoryApiController.class)
class AdminCategoryApiControllerTest extends RestDocsSupport {

	@MockBean
	private CategoryService categoryService;

	@DisplayName("[API][GET] 카테고리 조회")
	@Test
	void test_getCategories() throws Exception {
		//Given
		AdminCategoryResponse response1 = AdminCategoryResponse.of(
			1L,
			"상의",
			"상의입니다.",
			10,
			now(),
			"admin",
			now(),
			"admin"
		);
		AdminCategoryResponse response2 = AdminCategoryResponse.of(
			2L,
			"하의",
			"하의입니다.",
			20,
			now(),
			"admin",
			now(),
			"admin"
		);

		given(categoryService.findCategoriesForAdmin()).willReturn(List.of(response1, response2));

		//When & Then
		mvc.perform(get("/api/admin/categories"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").isArray())
			.andExpect(jsonPath("$.data[0].id").value(response1.id()))
			.andExpect(jsonPath("$.data[0].name").value(response1.name()))
			.andExpect(jsonPath("$.data[0].productCount").value(response1.productCount()))
			.andExpect(jsonPath("$.data[0].createdBy").value(response1.createdBy()))
			.andExpect(jsonPath("$.data[0].modifiedBy").value(response1.modifiedBy()))
			.andDo(document(
				"admin/category/getCategories",
				adminApiDescription(TagDescription.CATEGORY, "카테고리 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					STATUS,
					fieldWithPath("data[].id").type(JsonFieldType.NUMBER)
						.description(CATEGORY_ID.getDescription()),
					fieldWithPath("data[].name").type(JsonFieldType.STRING)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("data[].description").type(JsonFieldType.STRING)
						.description(CATEGORY_DESCRIPTION.getDescription()),
					fieldWithPath("data[].productCount").type(JsonFieldType.NUMBER)
						.description(CATEGORY_PRODUCT_COUNT.getDescription()),
					fieldWithPath("data[].createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data[].createdBy").type(JsonFieldType.STRING)
						.description(CREATED_BY.getDescription()),
					fieldWithPath("data[].modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription()),
					fieldWithPath("data[].modifiedBy").type(JsonFieldType.STRING)
						.description(MODIFIED_BY.getDescription())
				)
			));
		then(categoryService).should().findCategoriesForAdmin();
	}

	@DisplayName("[API][GET] 카테고리 상세 조회")
	@Test
	void test_getCategory() throws Exception {
		//Given
		AdminCategoryResponse response = AdminCategoryResponse.of(
			1L,
			"상의",
			"상의입니다.",
			10,
			now(),
			"admin",
			now(),
			"admin"
		);

		given(categoryService.findCategory(response.id())).willReturn(response);

		//When & Then
		mvc.perform(get("/api/admin/categories/{categoryId}", response.id()))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(response.id()))
			.andExpect(jsonPath("$.data.name").value(response.name()))
			.andExpect(jsonPath("$.data.productCount").value(response.productCount()))
			.andExpect(jsonPath("$.data.createdBy").value(response.createdBy()))
			.andExpect(jsonPath("$.data.modifiedBy").value(response.modifiedBy()))
			.andDo(document(
				"admin/category/getCategory",
				adminApiDescription(TagDescription.CATEGORY, "카테고리 상세 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("categoryId").description(CATEGORY_ID.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(CATEGORY_ID.getDescription()),
					fieldWithPath("data.name").type(JsonFieldType.STRING)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("data.description").type(JsonFieldType.STRING)
						.description(CATEGORY_DESCRIPTION.getDescription()),
					fieldWithPath("data.productCount").type(JsonFieldType.NUMBER)
						.description(CATEGORY_PRODUCT_COUNT.getDescription()),
					fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
						.description(CREATED_AT.getDescription()),
					fieldWithPath("data.createdBy").type(JsonFieldType.STRING)
						.description(CREATED_BY.getDescription()),
					fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING)
						.description(MODIFIED_AT.getDescription()),
					fieldWithPath("data.modifiedBy").type(JsonFieldType.STRING)
						.description(MODIFIED_BY.getDescription())
				)
			));
		then(categoryService).should().findCategory(response.id());
	}

	@DisplayName("[API][POST] 카테고리 추가")
	@Test
	void test_addCategory() throws Exception {
		//Given
		long id = 1L;
		CategoryRequest.Create request = CategoryRequest.Create.builder()
			.name("상의")
			.description("상의입니다.")
			.build();
		given(categoryService.addCategory(request.toServiceRequest())).willReturn(id);

		//When & Then
		mvc.perform(
				post("/api/admin/categories")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(id))
			.andDo(document(
				"admin/category/addCategory",
				adminApiDescription(TagDescription.CATEGORY, "카테고리 추가"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("name").type(JsonFieldType.STRING)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("description").type(JsonFieldType.VARIES)
						.description(CATEGORY_DESCRIPTION.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(ADD.getDescription() + CATEGORY_ID.getDescription())
				)
			));
		then(categoryService).should().addCategory(request.toServiceRequest());
	}

	@DisplayName("[API][PUT] 카테고리 수정")
	@Test
	void test_updateCategory() throws Exception {
		//Given
		long id = 1L;
		String name = "updated name";
		CategoryRequest.Update request = CategoryRequest.Update.builder()
			.name(name)
			.build();
		given(categoryService.updateCategory(id, request.toServiceRequest())).willReturn(id);

		//When & Then
		mvc.perform(
				put("/api/admin/categories/{categoryId}", id)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(id))
			.andDo(document(
				"admin/category/updateCategory",
				adminApiDescription(TagDescription.CATEGORY, "카테고리 수정"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("categoryId").description(CATEGORY_ID.getDescription())
				),
				requestFields(
					fieldWithPath("name").type(JsonFieldType.STRING)
						.description(CATEGORY_NAME.getDescription()),
					fieldWithPath("description").type(JsonFieldType.VARIES)
						.description(CATEGORY_DESCRIPTION.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(UPDATE.getDescription() + CATEGORY_ID.getDescription())
				)
			));
		then(categoryService).should().updateCategory(id, request.toServiceRequest());
	}

	@DisplayName("[API][DELETE] 카테고리 삭제")
	@Test
	void test_deleteCategory() throws Exception {
		//Given
		long id = 1L;
		given(categoryService.deleteCategory(id)).willReturn(id);

		//When & Then
		mvc.perform(delete("/api/admin/categories/{categoryId}", id))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(id))
			.andDo(document(
				"admin/category/deleteCategory",
				adminApiDescription(TagDescription.CATEGORY, "카테고리 삭제"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				pathParameters(
					parameterWithName("categoryId").description(CATEGORY_ID.getDescription())
				),
				responseFields(
					RestDocsUtils.STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(DELETE.getDescription() + CATEGORY_ID.getDescription())
				)
			));
		then(categoryService).should().deleteCategory(any());
	}
}
