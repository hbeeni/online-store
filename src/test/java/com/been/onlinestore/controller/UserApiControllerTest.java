package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.*;
import static java.time.LocalDateTime.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithUserDetails;

import com.been.onlinestore.config.TestSecurityConfig;
import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.service.UserService;
import com.been.onlinestore.service.dto.response.UserResponse;

@DisplayName("API 컨트롤러 - 회원")
@Import(TestSecurityConfig.class)
@WebMvcTest(UserApiController.class)
class UserApiControllerTest extends RestDocsSupport {

	@MockBean
	UserService userService;

	@WithUserDetails
	@DisplayName("[API][GET] 회원 상세 조회")
	@Test
	void test_getUser() throws Exception {
		//Given
		long id = 1L;
		String uid = "user1";
		UserResponse response = UserResponse.of(
			id,
			uid,
			"$2a$10$wcVfFiEQnqu3WjgyiIsPzuqdYKV9WJ08Wx.4aac0e08CLFpUjvoW6",
			"user1",
			uid + "@mail.com",
			"test user",
			"01012345678",
			RoleType.USER,
			LocalDateTime.now().minusDays(3),
			now()
		);

		given(userService.findUser(id)).willReturn(response);

		//When & Then
		mvc.perform(get("/api/users/info"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.id").value(response.id()))
			.andExpect(jsonPath("$.data.uid").value(response.uid()))
			.andExpect(jsonPath("$.data.name").value(response.name()))
			.andExpect(jsonPath("$.data.password").doesNotExist())
			.andDo(document(
				"user/user/getUser",
				userApiDescription(TagDescription.USER, "회원 상세 조회"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(USER_ID.getDescription()),
					fieldWithPath("data.uid").type(JsonFieldType.STRING)
						.description(USER_UID.getDescription()),
					fieldWithPath("data.name").type(JsonFieldType.STRING)
						.description(USER_NAME.getDescription()),
					fieldWithPath("data.email").type(JsonFieldType.STRING)
						.description(USER_EMAIL.getDescription()),
					fieldWithPath("data.nickname").type(JsonFieldType.STRING)
						.description(USER_NICKNAME.getDescription()),
					fieldWithPath("data.phone").type(JsonFieldType.STRING)
						.description(USER_PHONE.getDescription()),
					fieldWithPath("data.roleType").type(JsonFieldType.STRING)
						.description(USER_ROLE_TYPE.getDescription()),
					fieldWithPath("data.createdAt").type(JsonFieldType.STRING)
						.description(USER_CREATED_AT.getDescription()),
					fieldWithPath("data.modifiedAt").type(JsonFieldType.STRING)
						.description(USER_MODIFIED_AT.getDescription())
				)
			));
		then(userService).should().findUser(id);
	}

	@WithUserDetails
	@DisplayName("[API][PUT] 회원 정보 변경 - 닉네임과 휴대폰 번호 변경 가능")
	@Test
	void test_updateUserInfo() throws Exception {
		//Given
		Long id = TestSecurityConfig.USER_ID;
		UserRequest.Update request = new UserRequest.Update("nickname", "01012345678");

		given(userService.updateInfo(id, request.nickname(), request.phone())).willReturn(id);

		//When & Then
		mvc.perform(
				put("/api/users/info")
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
				"user/user/updateUser",
				userApiDescription(TagDescription.USER, "회원 정보 수정"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("nickname").type(JsonFieldType.STRING)
						.description(USER_NICKNAME.getDescription()),
					fieldWithPath("phone").type(JsonFieldType.STRING)
						.description(USER_PHONE.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description(UPDATE.getDescription() + USER_ID.getDescription())
				)
			));
		then(userService).should().updateInfo(id, request.nickname(), request.phone());
	}
}
