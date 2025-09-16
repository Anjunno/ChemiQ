package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.DailyMissionStatus;
import com.chemiq.entity.Partnership;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMissionRepository extends JpaRepository<DailyMission, Long> {

    //미션 날짜와 파트너십으로 해당 날짜 미션 찾
    Optional<DailyMission> findByPartnershipAndMissionDate(Partnership partnership, LocalDate today);

    // 파트너십을 기준으로 DailyMission을 날짜 내림차순으로 페이징 조회
    Page<DailyMission> findByPartnershipOrderByMissionDateDesc(Partnership partnership, Pageable pageable);

    // JOIN FETCH를 사용하여 연관된 Mission 엔티티를 함께 조회 (N+1 방지)
    @Query("SELECT dm FROM DailyMission dm JOIN FETCH dm.mission WHERE dm.partnership = :partnership AND dm.missionDate = :date")
    Optional<DailyMission> findByPartnershipAndMissionDateWithMission(
            @Param("partnership") Partnership partnership,
            @Param("date") LocalDate date
    );

    // 특정 파트너십의 '완료된' 미션 총 개수를 세는 메소드
    long countByPartnershipAndStatus(Partnership partnership, DailyMissionStatus status);

    // 특정 파트너십의, 특정 기간 동안의 '완료된' 미션 개수를 세는 메소드
    long countByPartnershipAndStatusAndMissionDateBetween(
            Partnership partnership,
            DailyMissionStatus status,
            LocalDate startDate,
            LocalDate endDate
    );

    // 특정 파트너십의, 시작일과 종료일 사이의 모든 DailyMission을 Mission 정보와 함께 조회 (N+1 방지)
    @Query("SELECT dm FROM DailyMission dm JOIN FETCH dm.mission WHERE dm.partnership = :partnership AND dm.missionDate BETWEEN :startDate AND :endDate")
    List<DailyMission> findAllByPartnershipAndDateRangeWithMission(
            @Param("partnership") Partnership partnership,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
