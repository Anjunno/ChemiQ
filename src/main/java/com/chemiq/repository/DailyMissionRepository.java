package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Partnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    //미션 날짜와 파트너십으로 해당 날짜 미션 찾
    Optional<DailyMission> findByPartnershipAndMissionDate(Partnership partnership, LocalDate today);

    // 파트너십을 기준으로 DailyMission을 날짜 내림차순으로 페이징 조회
    Page<DailyMission> findByPartnershipOrderByMissionDateDesc(Partnership partnership, Pageable pageable);
}
