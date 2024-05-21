package com.project.studylink.repository;

import com.project.studylink.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT r FROM Region r WHERE r.pullName LIKE %:keyword%")
    List<Region> searchByPullName(@Param("keyword") String keyword);
}
