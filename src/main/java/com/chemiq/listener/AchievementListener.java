package com.chemiq.listener;

import com.chemiq.entity.Member;
import com.chemiq.event.SubmissionCreatedEvent;
import com.chemiq.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component // Spring이 이 클래스를 Bean으로 관리하도록 설정
@RequiredArgsConstructor
public class AchievementListener {

    private final AchievementService achievementService; // 도전과제 처리 로직을 담은 서비스

    @EventListener //SubmissionCreatedEvent 타입의 이벤트가 발생하면 이 메소드가 자동으로 실행.
    public void onSubmissionCreated(SubmissionCreatedEvent event) {
        // 이벤트에서 Submission 객체를 꺼냅니다.
        Member submitter = event.getSubmission().getSubmitter();

        achievementService.checkAndGrantFirstSubmissionAchievement(submitter);
    }
}