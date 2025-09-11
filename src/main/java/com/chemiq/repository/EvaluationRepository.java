package com.chemiq.repository;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Evaluation;
import com.chemiq.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsBySubmission(Submission submission);

    // Submission 리스트에 해당하는 모든 Evaluation을 한 번에 조회
    List<Evaluation> findAllBySubmissionIn(List<Submission> submissions);

    // 특정 DailyMission에 속한 Submission들에 대한 Evaluation의 개수를 세는 쿼리
    @Query("SELECT count(e) FROM Evaluation e WHERE e.submission.dailyMission = :dailyMission")
    long countByDailyMission(@Param("dailyMission") DailyMission dailyMission);


}