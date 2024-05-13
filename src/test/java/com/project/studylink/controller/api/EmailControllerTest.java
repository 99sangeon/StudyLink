package com.project.studylink.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.studylink.config.SecurityConfig;
import com.project.studylink.dto.request.EmailAuthRequest;
import com.project.studylink.dto.request.EmailSendRequest;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.service.EmailService;
import com.project.studylink.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.project.studylink.enums.ErrorCode.*;
import static org.mockito.BDDMockito.*;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailController.class)
@AutoConfigureRestDocs
@Import(SecurityConfig.class)
class EmailControllerTest {

    private final static String API_V1 = "/api/v1/emails";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    MemberService memberService;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("인증번호 전송 성공")
    void sendAuthNum_success() throws Exception {
        // given
        EmailSendRequest emailSendRequest = EmailSendRequest.builder()
                .email("test@example.com")
                .build();

        String content = objectMapper.writeValueAsString(emailSendRequest);

        willDoNothing().given(memberService).checkUsernameDuplicate(anyString());
        willDoNothing().given(emailService).sendAuthMail(any(EmailSendRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/authNum/send")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("인증번호 전송 성공", "인증번호 전송"));
    }

    @Test
    @DisplayName("인증번호 전송 실패 - 이메일 중복")
    void sendAuthNum_fail_emailDuplicate() throws Exception {
        // given
        EmailSendRequest emailSendRequest = EmailSendRequest.builder()
                .email("test@example.com")
                .build();

        String content = objectMapper.writeValueAsString(emailSendRequest);

        willThrow(new BusinessException(EMAIL_DUPLICATE)).given(memberService).checkUsernameDuplicate(anyString());

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/authNum/send")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EMAIL_DUPLICATE.name()))
                .andExpect(jsonPath("$.error.message").value(EMAIL_DUPLICATE.getMessage()))
                .andDo(document("인증번호 전송 실패 - 이메일 중복"));

        then(emailService).should(never()).sendAuthMail(any());
    }

    @Test
    @DisplayName("인증번호 전송 실패 - 서버 내부 오류")
    void sendAuthNum_fail_serveError() throws Exception {
        // given
        EmailSendRequest emailSendRequest = EmailSendRequest.builder()
                .email("test@example.com")
                .build();

        String content = objectMapper.writeValueAsString(emailSendRequest);

        willDoNothing().given(emailService).sendAuthMail(any(EmailSendRequest.class));
        willThrow(new BusinessException(EMAIL_SEND_FAIL)).given(emailService).sendAuthMail(any(EmailSendRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/authNum/send")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EMAIL_SEND_FAIL.name()))
                .andExpect(jsonPath("$.error.message").value(EMAIL_SEND_FAIL.getMessage()))
                .andDo(document("인증번호 전송 실패 - 서버 내부 오류"));
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void validateAuthNum_success() throws Exception {
        // given
        EmailAuthRequest emailAuthRequest = EmailAuthRequest.builder()
                .email("test@example.com")
                .authNum("123456")
                .build();

        String content = objectMapper.writeValueAsString(emailAuthRequest);

        willDoNothing().given(emailService).validateAuthNum(any(EmailAuthRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/authNum/validate")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(document("이메일 인증 성공", "이메일 인증"));
    }

    @Test
    @DisplayName("이메일 인증 실패")
    void validateAuthNum_fail() throws Exception {
        // given
        EmailAuthRequest emailAuthRequest = EmailAuthRequest.builder()
                .email("test@example.com")
                .authNum("123456")
                .build();

        String content = objectMapper.writeValueAsString(emailAuthRequest);

        willThrow(new BusinessException(EMAIL_AUTH_FAIL)).given(emailService).validateAuthNum(any(EmailAuthRequest.class));

        // when
        ResultActions resultActions = mockMvc.perform(
                post(API_V1 + "/authNum/validate")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value(EMAIL_AUTH_FAIL.name()))
                .andExpect(jsonPath("$.error.message").value(EMAIL_AUTH_FAIL.getMessage()))
                .andDo(document("이메일 인증 실패"));
    }
}