package com.project.studylink.auth;

import com.project.studylink.entity.Member;
import com.project.studylink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.of(registrationId, userNameAttributeName, attributes);

        Member member = updateOrSaveThenGetMember(oAuth2UserInfo);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getValue())),
                attributes,
                userNameAttributeName
        );
    }

    private Member updateOrSaveThenGetMember(OAuth2UserInfo oAuth2UserInfo) {
        Member member = memberRepository.findByUsername(oAuth2UserInfo.getUsername())
                .map((m) -> updateMember(m, oAuth2UserInfo))
                .orElseGet(oAuth2UserInfo::toEntity);

        return memberRepository.save(member);
    }

    private Member updateMember(Member member, OAuth2UserInfo oAuth2UserInfo) {
        member.setEmail(oAuth2UserInfo.getEmail());
        return member;
    }
}
