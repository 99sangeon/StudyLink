package com.project.studylink.repository;

import com.project.studylink.entity.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RegionRepositoryTest {

    @Autowired
    private RegionRepository regionRepository;

    @Test
    @DisplayName("지역 검색")
    void searchByPullName() {
        // given
        Region region1 = Region.builder()
                .sido("경상남도")
                .sigg("창원시 의창구")
                .emd("의창동")
                .pullName("경상남도 창원시 의창구 의창동")
                .build();

        Region region2 = Region.builder()
                .sido("서울특별시")
                .sigg("동대문구")
                .emd("전농동")
                .pullName("서울특별시 동대문구 전농동")
                .build();

        regionRepository.save(region1);
        regionRepository.save(region2);

        String keyword1 = "의창동";
        String keyword2 = "전농동";
        String keyword3 = "한남동";
        String keyword4 = "";

        // when
        List<Region> regions1 = regionRepository.searchByPullName(keyword1);
        List<Region> regions2 = regionRepository.searchByPullName(keyword2);
        List<Region> regions3 = regionRepository.searchByPullName(keyword3);
        List<Region> regions4 = regionRepository.searchByPullName(keyword4);

        // then
        assertTrue(regions1.size() == 1);
        assertTrue(regions2.size() == 1);
        assertTrue(regions3.size() == 0);
        assertTrue(regions4.size() == 2);

        assertThat(regions1.get(0)).isEqualTo(region1);
        assertThat(regions2.get(0)).isEqualTo(region2);
    }
}