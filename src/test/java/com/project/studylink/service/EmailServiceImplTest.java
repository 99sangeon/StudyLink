package com.project.studylink.service;

import com.project.studylink.dto.request.EmailAuthRequest;
import com.project.studylink.dto.request.EmailSendRequest;
import com.project.studylink.enums.ErrorCode;
import com.project.studylink.exception.BusinessException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static com.project.studylink.enums.ErrorCode.EMAIL_AUTH_FAIL;
import static com.project.studylink.enums.ErrorCode.EMAIL_SEND_FAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Spy
    private JavaMailSenderImpl javaMailSender;

    @Spy
    private SpringTemplateEngine templateEngine;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Test
    @DisplayName("인증번호 전송 성공")
    void sendAuthMail_success() {
        // given
        String rightEmail = "test@example.com";
        EmailSendRequest emailSendRequest = EmailSendRequest.builder().email(rightEmail).build();

        willDoNothing().given(javaMailSender).send(any(MimeMessage.class));

        // when
        emailService.sendAuthMail(emailSendRequest);

        // then
        then(redisService).should(times(1)).save(eq(emailSendRequest.getEmail()), anyString(), anyInt());
    }

    @Test
    @DisplayName("인증번호 전송 실패")
    void sendAuthMail_fail() {
        // given
        // 잘못된 이메일 형식을 이용하여 MessagingException 의도적으로 발생 시킴
        String wrongEmail = "@example.com";
        EmailSendRequest emailSendRequest = EmailSendRequest.builder().email(wrongEmail).build();

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> emailService.sendAuthMail(emailSendRequest));

        // then
        assertEquals(exception.getErrorCode(), EMAIL_SEND_FAIL);

        then(javaMailSender).should(never()).send(any(MimeMessage.class));
        then(redisService).should(never()).save(anyString(), anyString(), anyInt());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void validateAuthNum_success() throws Exception {
        // given
        EmailAuthRequest emailAuthRequest = EmailAuthRequest.builder().email("test@example.com").build();

        willDoNothing().given(redisService).validateValue(emailAuthRequest.getEmail(), emailAuthRequest.getAuthNum());

        // when
        emailService.validateAuthNum(emailAuthRequest);


        then(redisService).should(times(1)).save(eq(emailAuthRequest.getEmail()), eq(emailAuthRequest.getAuthNum()), anyInt());
    }

    @Test
    @DisplayName("이메일 인증 실패")
    void validateAuthNum_fail() throws Exception {
        // given
        EmailAuthRequest emailAuthRequest = EmailAuthRequest.builder().email("test@example.com").build();

        willThrow(new Exception()).given(redisService).validateValue(emailAuthRequest.getEmail(), emailAuthRequest.getAuthNum());

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> emailService.validateAuthNum(emailAuthRequest));

        assertEquals(exception.getErrorCode(), EMAIL_AUTH_FAIL);

        then(redisService).should(never()).save(anyString(), anyString(), anyInt());
    }
}