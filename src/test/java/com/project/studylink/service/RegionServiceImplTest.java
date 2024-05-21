package com.project.studylink.service;

import com.project.studylink.dto.response.RegionResponse;
import com.project.studylink.entity.Region;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.project.studylink.enums.ErrorCode.EXCEL_NOT_READABLE;
import static com.project.studylink.enums.ErrorCode.REGION_CELL_NOT_READABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RegionServiceImplTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionServiceImpl regionService;

    @Test
    void registerRegion_Success() throws IOException {
        // given
        MockMultipartFile validRegionFile = new MockMultipartFile("regionFile",
                "region.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new FileInputStream("src/test/resources/static/valid_region_file.xlsx"));
        // when
        regionService.registerRegion(validRegionFile);

        // then
        then(regionRepository).should(times(1)).deleteAll();
        then(regionRepository).should(times(1)).saveAll(any());
    }

    @Test
    void registerRegion_fail_EXCEL_NOT_READABLE() throws IOException {
        // given
        MockMultipartFile invalidRegionFile = new MockMultipartFile("regionFile",
                "region.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new FileInputStream("src/test/resources/static/invalid_region_file1.png"));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> regionService.registerRegion(invalidRegionFile));

        // then
        assertEquals(exception.getErrorCode(), EXCEL_NOT_READABLE);
        then(regionRepository).should(never()).deleteAll();
        then(regionRepository).should(never()).saveAll(any());
    }

    @Test
    void registerRegion_fail_REGION_CELL_NOT_READABLE() throws IOException {
        // given
        MockMultipartFile invalidRegionFile = new MockMultipartFile("regionFile",
                "region.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new FileInputStream("src/test/resources/static/invalid_region_file2.xlsx"));

        // when
        BusinessException exception =
                assertThrows(BusinessException.class, () -> regionService.registerRegion(invalidRegionFile));

        // then
        assertEquals(exception.getErrorCode(), REGION_CELL_NOT_READABLE);
        then(regionRepository).should(never()).deleteAll();
        then(regionRepository).should(never()).saveAll(any());
    }

    @Test
    void searchRegions_Success() {
        // given
        String keyword = "서울";
        List<Region> regions = new ArrayList<>();
        regions.add(Region.builder().sido("서울특별시").sigg("강남구").emd("역삼동").pullName("서울특별시 강남구 역삼동").build());

        given(regionRepository.searchByPullName(keyword)).willReturn(regions);

        // when
        List<RegionResponse> result = regionService.searchRegions(keyword);

        // then
        assertEquals(1, result.size());
    }
}