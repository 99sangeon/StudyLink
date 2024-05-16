package com.project.studylink.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.request.LoginRequest;
import com.project.studylink.dto.response.JwtResponse;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.project.studylink.enums.ErrorCode.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
class AuthControllerTest {

    private final static String API_V1 = "/api/v1/auth";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // given
        LoginRequest loginRequest = creatLoginRequest();
        JwtResponse jwtResponse = createJwtResponse();
        String content = objectMapper.writeValueAsString(loginRequest);

        given(authService.login(any(LoginRequest.class))).willReturn(jwtResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(jwtResponse.getAccessToken()))
                .andExpect(jsonPath("$.data.refreshToken").value(jwtResponse.getRefreshToken()))
                .andDo(document("로그인 성공", "로그인 성공"));
    }

    @Test
    @DisplayName("로그인 실패 - 아이디 비밀번호 불일치")
    void login_fail() throws Exception {
        // given
        LoginRequest loginRequest = creatLoginRequest();
        String content = objectMapper.writeValueAsString(loginRequest);

        given(authService.login(any(LoginRequest.class))).willThrow(new BadCredentialsException("로그인 실패"));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(LOGIN_FAIL.name()))
                .andExpect(jsonPath("$.error.message").value(LOGIN_FAIL.getMessage()))
                .andDo(document("로그인 실패 - 아이디 비밀번호 불일치"));
    }

    @Test
    @DisplayName("JWT 재발급 성공")
    void reIssueAccessToken_success() throws Exception {
        // given
        JwtResponse jwtResponse = createJwtResponse();
        Cookie cookie = new Cookie("refreshToken", "refreshToken");

        given(authService.reIssueAccessToken(anyString())).willReturn(jwtResponse);

        // when
        ResultActions resultActions = mockMvc.perform(
                get(API_V1 + "/reissue/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(jwtResponse.getAccessToken()))
                .andExpect(jsonPath("$.data.refreshToken").value(jwtResponse.getRefreshToken()))
                .andDo(document("JWT 재발급 성공", "JWT 재발급 성공"));
    }

    @Test
    @DisplayName("JWT 재발급 실패 - 토큰 유효성 검증 실패")
    void reIssueAccessToken_fail() throws Exception {
        // given
        Cookie cookie = new Cookie("refreshToken", "refreshToken");

        given(authService.reIssueAccessToken(anyString())).willThrow(new BusinessException(REFRESH_TOKEN_VALIDATE_FAIL));

        // when
        ResultActions resultActions = mockMvc.perform(
                get(API_V1 + "/reissue/token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(REFRESH_TOKEN_VALIDATE_FAIL.name()))
                .andExpect(jsonPath("$.error.message").value(REFRESH_TOKEN_VALIDATE_FAIL.getMessage()))
                .andDo(document("JWT 재발급 실패 - 토큰 유효성 검증 실패"));
    }

    private static LoginRequest creatLoginRequest() {
        return LoginRequest.builder()
                .email("test@example.com")
                .password("testPassword")
                .build();
    }

    private JwtResponse createJwtResponse() {
        return JwtResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }
}