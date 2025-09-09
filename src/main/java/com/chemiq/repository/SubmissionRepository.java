package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Member;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // 특정 DailyMission에 특정 사용자가 제출한 기록이 있는지 확인
    boolean existsByDailyMissionAndSubmitter(DailyMission dailyMission, Member submitter);

    @Query("SELECT s FROM Submission s JOIN s.dailyMission dm WHERE dm.partnership = :partnership")
    Page<Submission> findByPartnership(@Param("partnership") Partnership partnership, Pageable pageable);

    // 여러 DailyMission에 속한 모든 Submission들을 한 번에 조회 (N+1 문제 방지용)
    List<Submission> findAllByDailyMissionIn(List<DailyMission> dailyMissions);
}