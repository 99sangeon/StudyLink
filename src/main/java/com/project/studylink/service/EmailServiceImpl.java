package com.project.studylink.service;

import com.project.studylink.dto.request.EmailAuthRequest;
import com.project.studylink.dto.request.EmailSendRequest;
import com.project.studylink.enums.ErrorCode;
import com.project.studylink.exception.BusinessException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisService redisService;

    @Override
    public void sendAuthMail(EmailSendRequest emailSendRequest) {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        String authNum = createAuthNum();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailSendRequest.getEmail()); // 메일 수신자
            mimeMessageHelper.setSubject("스터디링크 이메일 인증"); // 메일 제목
            mimeMessageHelper.setText(createAuthContext(authNum), true); // 메일 본문 내용, HTML 여부

            log.info("success send mail");

        } catch (MessagingException e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAIL);
        }

        // 이메일 전송
        javaMailSender.send(mimeMessage);

        // 레디스에 이메일-인증번호 저장
        redisService.save(emailSendRequest.getEmail(), authNum, 10);
    }

    @Override  // 사용자가 직접 이메일 인증 요청
    public void validateAuthNum(EmailAuthRequest emailAuthRequest) {
        validateAuthNum(emailAuthRequest.getEmail(), emailAuthRequest.getAuthNum());

        // 인증 후 이메일-인증번호 유효 시간 30분으로 연장 -> 즉, 30분 내에 회원가입 해야함
        redisService.save(emailAuthRequest.getEmail(), emailAuthRequest.getAuthNum(), 30);
    }

    @Override  // 회원가입 최종 검증 과정에서 이메일 인증
    public void validateAuthNum(String email, String authNum) {
        if(!redisService.validateValue(email, authNum)) {
            throw new BusinessException(ErrorCode.EMAIL_AUTH_FAIL);
        }
    }

    private String createAuthNum() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }


    private String createAuthContext(String authNum) {
        Context context = new Context();
        context.setVariable("authNum", authNum);
        return templateEngine.process("/mail-auth", context);
    }

}