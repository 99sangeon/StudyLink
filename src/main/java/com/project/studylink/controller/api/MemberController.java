package com.project.studylink.controller.api;

import com.project.studylink.dto.response.ApiResponse;
import com.project.studylink.dto.request.MemberRequest;
import com.project.studylink.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final static String API_V1 = "/api/v1/members";

    private final MemberService memberService;

    @PostMapping(API_V1)
    public ResponseEntity<?> signUpV1(@RequestBody @Valid MemberRequest memberRequest) {
        memberService.signUp(memberRequest);
        return ResponseEntity.ok(ApiResponse.success());
    }
}