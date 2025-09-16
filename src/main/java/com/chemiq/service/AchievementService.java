package com.chemiq.service;

import com.chemiq.entity.Member;
import com.chemiq.entity.MemberAchievement;
import com.chemiq.repository.AchievementRepository;
import com.chemiq.repository.MemberAchievementRepository;
import com.chemiq.repository.SubmissionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final SubmissionRepository submissionRepository;
    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);

    // 처음 퀘스트를 제출했을 때 [첫 발걸음] 도전과제 달성
    public void checkAndGrantFirstSubmissionAchievement(Member submitter) {

        // 1.  이 사용자가 "FIRST_SUBMISSION" 도전과제를 이미 달성했는지 먼저 확인합니다.
        if (memberAchievementRepository.existsByMemberAndAchievement_Code(submitter, "FIRST_SUBMISSION")) {
            return; // 이미 달성했다면, 더 이상 아무것도 하지 않고 즉시 종료
        }

        // 2. [기존 로직] 달성하지 않았을 때만, 조건을 만족하는지 확인합니다.
        long submissionCount = submissionRepository.countBySubmitter(submitter);

        if (submissionCount == 1) {
            achievementRepository.findByCode("FIRST_SUBMISSION").ifPresent(achievement -> {
                // 중복 확인 로직은 위에서 이미 처리했으므로 바로 저장
                MemberAchievement newAchievement = MemberAchievement.builder()
                        .member(submitter)
                        .achievement(achievement)
                        .build();
                memberAchievementRepository.save(newAchievement);
                log.info("사용자 {}에게 '{}' 도전과제가 부여되었습니다.", submitter.getMemberId(), achievement.getName());
            });
        }
    }
}
