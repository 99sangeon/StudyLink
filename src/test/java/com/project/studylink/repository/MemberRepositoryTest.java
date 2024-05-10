package com.project.studylink.repository;

import com.project.studylink.entity.Member;
import com.project.studylink.enums.Role;
import com.project.studylink.enums.Sns;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 등록")
    void save() {
        // given
        Member member = createMember();

        // when
        Member saveMember = memberRepository.save(member);

        // then
        assertNotNull(saveMember.getId());
        assertEquals(saveMember.getUsername(), member.getUsername());
        assertEquals(saveMember.getPassword(), member.getPassword());
        assertEquals(saveMember.getEmail(), member.getEmail());
        assertEquals(saveMember.getNickname(), member.getNickname());
        assertEquals(saveMember.getRole(), member.getRole());
        assertEquals(saveMember.getSns(), member.getSns());
        assertEquals(saveMember.getProfileImg(), member.getProfileImg());
    }

    @Test
    @DisplayName("회원 조회")
    void findByUserName() {
        // given
        Member member = createMember();
        memberRepository.save(member);

        // when
        Optional<Member> findMember1 = memberRepository.findByUsername(member.getUsername());
        Optional<Member> findMember2 = memberRepository.findByUsername("notExistUsername");

        // then
        assertTrue(findMember1.isPresent());
        assertTrue(findMember2.isEmpty());
    }

    private Member createMember() {
        Member member = Member.builder()
                .username("testUsername")
                .password("testPassword")
                .email("tset@example.com")
                .nickname("testNickname")
                .introduction("testIntroduction")
                .build();

        member.setRole(Role.MEMBER);
        member.setSns(Sns.NONE);
        member.setProfileImg("/testProfileImg");

        return member;
    }
}