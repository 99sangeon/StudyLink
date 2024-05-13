package com.project.studylink.controller.api;

import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.request.EmailAuthRequest;
import com.project.studylink.dto.request.EmailSendRequest;
import com.project.studylink.service.EmailService;
import com.project.studylink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final static String API_V1 = "/api/v1/emails";

    private final EmailService emailService;
    private final MemberService memberService;

    @PostMapping(API_V1 + "/authNum/send")
    public ResponseEntity<?> sendAuthNum(@RequestBody @Valid EmailSendRequest emailSendRequest) {
        memberService.checkUsernameDuplicate(emailSendRequest.getEmail());
        emailService.sendAuthMail(emailSendRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping(API_V1 + "/authNum/validate")
    public ResponseEntity<?> validateAuthNum(@RequestBody @Valid EmailAuthRequest emailAuthRequest) {
        emailService.validateAuthNum(emailAuthRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }
}