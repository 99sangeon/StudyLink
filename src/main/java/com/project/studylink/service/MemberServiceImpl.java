package com.project.studylink.service;

import com.project.studylink.dto.request.MemberRequest;
import com.project.studylink.entity.Member;
import com.project.studylink.enums.Role;
import com.project.studylink.enums.Sns;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.project.studylink.enums.ErrorCode.EMAIL_DUPLICATE;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RedisService redisService;

    @Override
    public Long signUp(MemberRequest memberRequest) {
        // 회원가입 조건 검증
        validateSignUpCondition(memberRequest);

        Member member = memberRequest.toEntity();
        member.setPassword(passwordEncoder.encode(memberRequest.getPassword()));
        member.setRole(Role.MEMBER);
        member.setSns(Sns.NONE);
        member.setProfileImg("default_profile.jpg");

        Long id = memberRepository.save(member).getId();

        // 회원가입 성공 시 레디스에서 관련 데이터 삭제
        redisService.deleteKey(memberRequest.getEmail());

        return id;
    }

    @Override
    @Transactional(readOnly = true)
    public void checkUsernameDuplicate(String username) {
        Optional<Member> findMember = memberRepository.findByUsername(username);

        if(findMember.isPresent()) {
            throw new BusinessException(EMAIL_DUPLICATE);
        }
    }

    private void validateSignUpCondition(MemberRequest memberRequest) {
        // 일반 회원가입 시 회원 고유 값(username) -> email
        checkUsernameDuplicate(memberRequest.getEmail());
        emailService.validateAuthNum(memberRequest.getEmail(), memberRequest.getAuthNum());
    }
}