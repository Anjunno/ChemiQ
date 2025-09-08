package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Member;
import com.chemiq.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    // 특정 DailyMission에 특정 사용자가 제출한 기록이 있는지 확인
    boolean existsByDailyMissionAndSubmitter(DailyMission dailyMission, Member submitter);
}