package com.project.studylink.entity;

import com.project.studylink.enums.Role;
import com.project.studylink.enums.Sns;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor
@DynamicInsert
public class Member extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    // 회원 인식 고유 값
    @Column(nullable = false, unique = true)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(nullable = false)
    private String email;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @Setter
    @Column(nullable = false)
    @ColumnDefault("''")
    private String introduction;

    @Setter
    @Column(nullable = false)
    @ColumnDefault("'default_profile.jpg'")
    private String profileImg;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Sns sns;

    @Builder
    public Member(String username, String password, String email, String nickname, String introduction, String profileImg, Role role, Sns sns) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
        this.profileImg = profileImg;
        this.role = role;
        this.sns = sns;
    }
}