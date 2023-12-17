package com.been.onlinestore.controller.restdocs;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.ParameterDescriptor;

import com.epages.restdocs.apispec.ResourceSnippetDetails;

public abstract class RestDocsUtils {

	public static final ParameterDescriptor[] PAGE_REQUEST_PARAM = {
		parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
		parameterWithName("size").description("한 페이지 당 사이즈").optional(),
		parameterWithName("sort").description("정렬 기준").optional()
	};
	public static final FieldDescriptor STATUS =
		fieldWithPath("status").type(JsonFieldType.STRING).description("응답 상태 코드");
	public static final FieldDescriptor[] PAGE_INFO = {
		fieldWithPath("page.number").type(JsonFieldType.NUMBER).description("현재 페이지"),
		fieldWithPath("page.size").type(JsonFieldType.NUMBER).description("한 페이지당 사이즈"),
		fieldWithPath("page.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
		fieldWithPath("page.totalElements").type(JsonFieldType.NUMBER).description("전체 데이터 수")
	};

	public static ResourceSnippetDetails homeApiDescription(TagDescription tag, String description) {
		return resourceDetails()
			.tag("[홈] " + tag.getTagName() + " API")
			.summary(description);
	}

	public static ResourceSnippetDetails userApiDescription(TagDescription tag, String description) {
		return resourceDetails()
			.tag("[일반 회원] " + tag.getTagName() + " API")
			.summary(description);
	}

	public static ResourceSnippetDetails adminApiDescription(TagDescription tag, String description) {
		return resourceDetails()
			.tag("[어드민] " + tag.getTagName() + " API")
			.summary(description);
	}
}
