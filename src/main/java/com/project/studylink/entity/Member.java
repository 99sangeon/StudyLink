package com.project.studylink.entity;

import com.project.studylink.enums.Role;
import com.project.studylink.enums.Sns;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    // 회원 인식 고유 값
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    @Setter
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String introduction;

    @Column
    @Setter
    private String profileImg;

    @Column(nullable = false)
    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Setter
    @Enumerated(EnumType.STRING)
    private Sns sns;

    @Builder
    public Member(String username, String password, String email, String nickname, String introduction) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
    }
}