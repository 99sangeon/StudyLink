package com.project.studylink.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    // 시도명
    private String sido;

    // 시군구명
    private String sigg;

    // 읍면동명
    private String emd;

    // 시도명 + 시군구명 + 읍면동명
    private String pullName;

    @Builder
    public Region(String sido, String sigg, String emd, String pullName) {
        this.sido = sido;
        this.sigg = sigg;
        this.emd = emd;
        this.pullName = pullName;
    }
}
