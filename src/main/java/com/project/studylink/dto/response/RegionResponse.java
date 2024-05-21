package com.project.studylink.dto.response;

import com.project.studylink.entity.Region;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegionResponse {
    private Long id;
    private String emd;
    private String pullName;

    public static RegionResponse of(Region region) {
        return RegionResponse.builder()
                .id(region.getId())
                .emd(region.getEmd())
                .pullName(region.getPullName())
                .build();
    }
}
