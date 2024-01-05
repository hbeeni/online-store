package com.been.onlinestore.controller;

import static com.been.onlinestore.controller.restdocs.FieldDescription.*;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.service.UserService;

@DisplayName("API 컨트롤러 - 회원가입, 로그인/로그아웃")
@ActiveProfiles("test")
@SpringBootTest
class AuthApiControllerTest extends RestDocsSupport {

	@Autowired
	private WebApplicationContext context;
	@Autowired
	private RestDocumentationContextProvider restDocumentation;

	@MockBean
	UserService userService;
	@MockBean
	AuthenticationManager authenticationManager;

	@DisplayName("[API][POST] 회원 가입")
	@Test
	void test_signUp() throws Exception {
		//Given
		long id = 1L;
		UserRequest.SignUp request =
			new UserRequest.SignUp("user", "password", "user", "user@mail.com", "user", "01012345678");

		given(userService.signUp(eq(request.toServiceRequest()), anyString())).willReturn(id);

		//When & Then
		mvc.perform(
				post("/api/sign-up")
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
				"home/auth/signUp",
				homeApiDescription(TagDescription.AUTH, "회원가입"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("uid").type(JsonFieldType.STRING).description(USER_UID.getDescription()),
					fieldWithPath("password").type(JsonFieldType.STRING)
						.description(USER_PASSWORD.getDescription()),
					fieldWithPath("name").type(JsonFieldType.STRING)
						.description(USER_NAME.getDescription()),
					fieldWithPath("email").type(JsonFieldType.STRING)
						.description(USER_EMAIL.getDescription()),
					fieldWithPath("nickname").type(JsonFieldType.STRING)
						.description(USER_NICKNAME.getDescription()),
					fieldWithPath("phone").type(JsonFieldType.STRING)
						.description(USER_PHONE.getDescription())
				),
				responseFields(
					STATUS,
					fieldWithPath("data.id").type(JsonFieldType.NUMBER)
						.description("가입 성공 " + USER_ID.getDescription())
				)
			));
		then(userService).should().signUp(eq(request.toServiceRequest()), anyString());
	}

	@DisplayName("[API][POST] 로그인")
	@Test
	void test_login() throws Exception {
		//Given
		UserRequest.Login request = new UserRequest.Login("user1", "test12");

		PrincipalDetails principalDetails = PrincipalDetails.of(
			1L,
			request.uid(),
			request.password(),
			RoleType.USER,
			"user",
			"user@mail.com",
			"user",
			"01012345678"
		);
		UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(
			principalDetails, request.password(), Set.of(new SimpleGrantedAuthority(RoleType.USER.getRoleName()))
		);
		given(authenticationManager.authenticate(any())).willReturn(authentication);

		//When & Then
		mvc.perform(
				post("/api/login")
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.characterEncoding("UTF-8")
					.content(mapper.writeValueAsString(request))
			)
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andDo(document(
				"home/auth/login",
				homeApiDescription(TagDescription.AUTH, "로그인"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				requestFields(
					fieldWithPath("uid").type(JsonFieldType.STRING).description(USER_UID.getDescription()),
					fieldWithPath("password").type(JsonFieldType.STRING)
						.description(USER_PASSWORD.getDescription())
				),
				responseFields(STATUS)
			));
	}

	@WithMockUser
	@DisplayName("[API][POST] 로그아웃")
	@Test
	void test_logout() throws Exception {
		//Given
		MockMvc mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity())
			.apply(documentationConfiguration(restDocumentation))
			.alwaysDo(print())
			.addFilters(new CharacterEncodingFilter("UTF-8", true))
			.build();

		//When & Then
		mvc.perform(post("/api/logout"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.status").value("success"))
			.andDo(document(
				"home/auth/logout",
				homeApiDescription(TagDescription.AUTH, "로그아웃"),
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()),
				responseFields(STATUS)
			));
	}
}
