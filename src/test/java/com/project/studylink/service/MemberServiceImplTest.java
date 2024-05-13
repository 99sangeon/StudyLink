package com.project.studylink.service;

import com.project.studylink.dto.request.MemberRequest;
import com.project.studylink.entity.Member;
import com.project.studylink.enums.ErrorCode;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RedisService redisService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("회원가입 성공")
    void signUp_success() {
        // given
        MemberRequest memberRequest = createMemberRequest();

        given(memberRepository.save(any(Member.class))).willReturn(memberRequest.toEntity());
        given(memberRepository.findByUsername(memberRequest.getEmail())).willReturn(Optional.empty());
        given(passwordEncoder.encode(memberRequest.getPassword())).willReturn("encodedPassword");

        willDoNothing().given(emailService).validateAuthNum(memberRequest.getEmail(), memberRequest.getAuthNum());

        // when
        memberService.signUp(memberRequest);

        // then
        then(redisService).should(times(1)).deleteKey(memberRequest.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_fail_emailDuplicate() {
        // given
        MemberRequest memberRequest = createMemberRequest();

        given(memberRepository.findByUsername(memberRequest.getEmail())).willReturn(Optional.of(new Member()));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> memberService.signUp(memberRequest));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.EMAIL_DUPLICATE);

        then(memberRepository).should(never()).save(any());
        then(redisService).should(never()).deleteKey(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 인증번호 불일치")
    void signUp_fail_authFail() {
        // given
        MemberRequest memberRequest = createMemberRequest();

        given(memberRepository.findByUsername(memberRequest.getEmail())).willReturn(Optional.empty());

        willThrow(new BusinessException(ErrorCode.EMAIL_AUTH_FAIL))
                .given(emailService).validateAuthNum(memberRequest.getEmail(), memberRequest.getAuthNum());


        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> memberService.signUp(memberRequest));

        // then
        assertEquals(exception.getErrorCode(), ErrorCode.EMAIL_AUTH_FAIL);

        then(memberRepository).should(never()).save(any());
        then(redisService).should(never()).deleteKey(any());
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