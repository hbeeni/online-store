package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.UserRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.controller.restdocs.RestDocsSupport;
import com.been.onlinestore.controller.restdocs.TagDescription;
import com.been.onlinestore.domain.constant.RoleType;
import com.been.onlinestore.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_EMAIL;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_ID;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_NAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_NICKNAME;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_PASSWORD;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_PHONE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_ROLE_TYPE;
import static com.been.onlinestore.controller.restdocs.FieldDescription.USER_UID;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.STATUS;
import static com.been.onlinestore.controller.restdocs.RestDocsUtils.homeApiDescription;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("API 컨트롤러 - 회원가입, 로그인")
@ActiveProfiles("test")
@SpringBootTest
class AuthApiControllerTest extends RestDocsSupport {

    @MockBean UserService userService;
    @MockBean AuthenticationManager authenticationManager;

    @DisplayName("[API][POST] 회원 가입")
    @Test
    void test_signUp() throws Exception {
        //Given
        long id = 1L;
        UserRequest.SignUp request = UserRequest.SignUp.builder()
                .uid("user")
                .password("password")
                .name("user")
                .email("user@mail.com")
                .nickname("user")
                .phone("01012345678")
                .build();
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
                                fieldWithPath("password").type(JsonFieldType.STRING).description(USER_PASSWORD.getDescription()),
                                fieldWithPath("name").type(JsonFieldType.STRING).description(USER_NAME.getDescription()),
                                fieldWithPath("email").type(JsonFieldType.STRING).description(USER_EMAIL.getDescription()),
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description(USER_NICKNAME.getDescription()),
                                fieldWithPath("phone").type(JsonFieldType.STRING).description(USER_PHONE.getDescription()),
                                fieldWithPath("roleType").type(JsonFieldType.STRING).description(USER_ROLE_TYPE.getDescription()).optional()
                        ),
                        responseFields(
                                STATUS,
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("가입 성공 " + USER_ID.getDescription())
                        )
                ));
        then(userService).should().signUp(eq(request.toServiceRequest()), anyString());
    }

    @DisplayName("[API][POST] 로그인")
    @Test
    void test_login() throws Exception {
        //Given
        UserRequest.Login request = UserRequest.Login.builder()
                .uid("user")
                .password("password")
                .build();

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
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.authenticated(principalDetails, request.password(), Set.of(new SimpleGrantedAuthority(RoleType.USER.getRoleName())));
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
                .andExpect(jsonPath("$.data.token").exists())
                .andDo(document(
                        "home/auth/login",
                        homeApiDescription(TagDescription.AUTH, "로그인"),
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("uid").type(JsonFieldType.STRING).description(USER_UID.getDescription()),
                                fieldWithPath("password").type(JsonFieldType.STRING).description(USER_PASSWORD.getDescription())
                        ),
                        responseFields(
                                STATUS,
                                fieldWithPath("data.token").type(JsonFieldType.STRING).description("JWT 토큰")
                        )
                ));
    }
}
