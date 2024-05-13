package com.project.studylink.service;

import com.project.studylink.dto.request.EmailAuthRequest;
import com.project.studylink.dto.request.EmailSendRequest;

public interface EmailService {
    void sendAuthMail(EmailSendRequest emailSendRequest);

    void validateAuthNum(EmailAuthRequest emailAuthRequest);

    void  validateAuthNum(String email, String authNum);
}
