package com.project.studylink.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.dto.request.MemberRequest;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.project.studylink.enums.ErrorCode.EMAIL_AUTH_FAIL;
import static com.project.studylink.enums.ErrorCode.EMAIL_DUPLICATE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MemberController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestDocs
class MemberControllerTest {

    private final static String API_V1 = "/api/v1/members";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() throws Exception {
        // given
        MemberRequest memberRequest = createMemberRequest();
        String content = objectMapper.writeValueAsString(memberRequest);

        given(memberService.signUp(any(MemberRequest.class))).willReturn(1L);

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("회원가입 성공", "회원가입"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_fail_emailDuplicate() throws Exception {
        // given
        MemberRequest memberRequest = createMemberRequest();
        String content = objectMapper.writeValueAsString(memberRequest);

        given(memberService.signUp(any(MemberRequest.class))).willThrow(new BusinessException(EMAIL_DUPLICATE));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EMAIL_DUPLICATE.name()))
                .andExpect(jsonPath("$.error.message").value(EMAIL_DUPLICATE.getMessage()))
                .andDo(document("회원가입 실패 - 이메일 중복"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 인증번호 불일치")
    void signUp_fail_authFail() throws Exception {
        // given
        MemberRequest memberRequest = createMemberRequest();
        String content = objectMapper.writeValueAsString(memberRequest);

        given(memberService.signUp(any(MemberRequest.class))).willThrow(new BusinessException(EMAIL_AUTH_FAIL));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EMAIL_AUTH_FAIL.name()))
                .andExpect(jsonPath("$.error.message").value(EMAIL_AUTH_FAIL.getMessage()))
                .andDo(document("회원가입 실패 - 이메일 인증번호 불일치"));;
    }

    private MemberRequest createMemberRequest() {
        return MemberRequest.builder()
                .email("test@example.com")
                .password("password123@")
                .nickname("testNickname")
                .introduction("testIntroduction")
                .authNum("123456")
                .build();
    }
}