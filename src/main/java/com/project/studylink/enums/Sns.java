package com.project.studylink.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Sns {

    NONE("NONE"),
    GOOGLE("GOOGLE"),
    KAKAO("KAKAO");

    private final String value;

}