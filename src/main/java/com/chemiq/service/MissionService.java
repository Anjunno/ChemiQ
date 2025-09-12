package com.chemiq.service;

import com.chemiq.DTO.TodayMissionResponse;
import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Mission;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.PartnershipStatus;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.EvaluationRepository;
import com.chemiq.repository.MissionRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {
    private static final Logger log = LoggerFactory.getLogger(MissionService.class);
    private final MissionRepository missionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final PartnershipRepository partnershipRepository;
    private final EvaluationRepository evaluationRepository;

    @Transactional
    // 매일 00시에 수행
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    // 1분마다
//    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul")
    public void assignDailyMissionToAllPartnerships() {
        log.info("데일리 미션 스케줄러 시작...");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 모든 활성 파트너십을 조회.
        List<Partnership> allActivePartnerships = partnershipRepository.findAllByStatus(PartnershipStatus.ACCEPTED);

        if (allActivePartnerships.isEmpty()) {
            log.info("미션을 할당할 파트너가 없어 스케줄러를 종료합니다.");
            return;
        }

        // --- 1. 어제 미션 완료 여부 확인 및 스트릭 초기화 ---
        log.info("어제 미션 완료 여부를 확인합니다...");
        for (Partnership partnership : allActivePartnerships) {
            dailyMissionRepository.findByPartnershipAndMissionDate(partnership, yesterday)
                    .ifPresent(yesterdayMission -> {
                        if (!checkMissionCompletion(yesterdayMission)) {
                            log.info("파트너십 ID {}: 어제 미션 미완료로 스트릭을 초기화합니다.", partnership.getId());
                            partnership.resetStreak();

                            partnership.applyScorePenalty(0.2); //0.2점 차감
                            log.info("파트너십 ID {}: 케미 지수 패널티가 적용되었습니다. 현재 점수: {}", partnership.getId(), partnership.getChemiScore());
                        }
                    });
        }

        // --- 2. 오늘의 미션 할당 ---
        log.info("오늘의 미션 할당을 시작합니다...");
        Optional<Mission> randomMissionOpt = missionRepository.findRandomMission();

        if (randomMissionOpt.isEmpty()) {
            log.warn("할당할 미션이 DB에 존재하지 않아 미션 할당을 건너뜁니다.");
            return;
        }
        Mission selectedMission = randomMissionOpt.get();
        log.info("오늘의 미션으로 '{}'가 선택되었습니다.", selectedMission.getTitle());

        // Stream API를 사용하여 더 간결하게 DailyMission 리스트 생성
        List<DailyMission> dailyMissions = allActivePartnerships.stream()
                .map(partnership -> DailyMission.builder()
                        .partnership(partnership)
                        .mission(selectedMission)
                        .missionDate(today)
                        .build())
                .toList();

        dailyMissionRepository.saveAll(dailyMissions);
        log.info("{} 날짜의 미션이 {}개의 파트너십에 성공적으로 할당되었습니다.", today, dailyMissions.size());
    }

    // 오늘의 미션 요청 메서드
    public TodayMissionResponse getTodayMission(Long memberNo) {

        // 1. 파트너 존재여부 확인
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("파트너가 존재하지 않습니다."));


        // 2. 해당 파트너십의 오늘의 미션
        LocalDate today = LocalDate.now();
        DailyMission todayMission = dailyMissionRepository.findByPartnershipAndMissionDate(partnership,today)
                .orElseThrow(() -> new EntityNotFoundException("오늘 할당된 미션이 없습니다."));

        // 3. 미션 내용 반환
        return new TodayMissionResponse(todayMission);
    }

    // 미션 완료 여부를 확인하는 헬퍼(helper) 메소드
    private boolean checkMissionCompletion(DailyMission dailyMission) {
        long evaluationCount = evaluationRepository.countByDailyMission(dailyMission);
        return evaluationCount >= 2;
    }
}
