package com.project.studylink.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Category extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String logoEmoji;

    @Setter
    @Column(nullable = false, unique = true)
    private String name;

    @Builder
    public Category(String logoEmoji, String name) {
        this.logoEmoji = logoEmoji;
        this.name = name;
    }
}
