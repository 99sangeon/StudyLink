package com.project.studylink.service;

import com.project.studylink.dto.response.RegionResponse;
import com.project.studylink.entity.Region;
import com.project.studylink.exception.BusinessException;
import com.project.studylink.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.project.studylink.enums.ErrorCode.EXCEL_NOT_READABLE;
import static com.project.studylink.enums.ErrorCode.REGION_CELL_NOT_READABLE;

@Service
@RequiredArgsConstructor
@Transactional
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    @Override
    public void registerRegion(MultipartFile regionFile) {
        List<Region> regions = createRegionList(regionFile);
        regionRepository.deleteAll();
        regionRepository.saveAll(regions);
    }

    @Override
    public List<RegionResponse> searchRegions(String keyword) {
        List<Region> regions = regionRepository.searchByPullName(keyword);
        List<RegionResponse> regionResponses = new ArrayList<>();

        for(Region region : regions) {
            regionResponses.add(RegionResponse.of(region));
        }

        return regionResponses;
    }

    private List<Region> createRegionList(MultipartFile regionFile) {
        // 엑셀 파일에서 모든 행 추출
        Iterator<Row> rows = getRowIterator(regionFile);
        // 최상단의 행에서 시도, 시군구, 읍면동의 셀 인덱스 추출
        int[] titleIdxs = getTitleIdxs(rows.next());

        int sidoIdx = titleIdxs[0];
        int siggIdx = titleIdxs[1];
        int emdIdx = titleIdxs[2];

        List<Region> regions = new ArrayList<>();

        // region 엔티티를 만든 후 regionList에 추가
        while(rows.hasNext()) {
            Row currRow = rows.next();
            String sido = "";
            String sigg = "";
            String emd = "";

            if(currRow.getCell(sidoIdx) != null) {
                sido = currRow.getCell(sidoIdx).getStringCellValue();
            }

            if(currRow.getCell(siggIdx) != null) {
                sigg = currRow.getCell(siggIdx).getStringCellValue();
            }

            if(currRow.getCell(emdIdx) != null) {
                emd = currRow.getCell(emdIdx).getStringCellValue();
            }

            if(!StringUtils.hasText(emd)) continue;

            String pullName = sido + " " + sigg + " " + emd;

            Region region = Region.builder()
                    .sido(sido)
                    .sigg(sigg)
                    .emd(emd)
                    .pullName(pullName)
                    .build();

            regions.add(region);
        }

        return regions;
    }

    private Iterator<Row> getRowIterator(MultipartFile regionFile) {
        Workbook workbook = null;

        try {
            workbook = new XSSFWorkbook(regionFile.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(EXCEL_NOT_READABLE);
        }

        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        return rows;
    }

    private int[] getTitleIdxs(Row titleRow) {
        int[] titleIdxs = new int[3];
        Arrays.fill(titleIdxs, -1);

        for(int i = titleRow.getFirstCellNum(); i < titleRow.getLastCellNum(); i++) {
            String title = titleRow.getCell(i).getStringCellValue();

            switch (title) {
                case "시도명":
                    titleIdxs[0] = i;
                    break;
                case "시군구명":
                    titleIdxs[1] = i;
                    break;
                case "읍면동명":
                    titleIdxs[2] = i;
            }
        }

        if(titleIdxs[0] == -1 || titleIdxs[1] == -1 || titleIdxs[2] == -1) {
            throw new BusinessException(REGION_CELL_NOT_READABLE);
        }

        return titleIdxs;
    }
}
