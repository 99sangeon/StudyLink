package com.project.studylink.service;

import com.project.studylink.dto.request.MemberRequest;

public interface MemberService {
    Long signUp(MemberRequest memberRequest);

    void checkUsernameDuplicate(String username);

}