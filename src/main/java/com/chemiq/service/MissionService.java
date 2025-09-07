package com.chemiq.service;

import com.chemiq.DTO.TodayMissionResponse;
import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Mission;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.PartnershipStatus;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.MissionRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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


    @Transactional
    // 매일 00시에 수행
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    // 1분마다
//    @Scheduled(cron = "0 * * * * ?", zone = "Asia/Seoul")
    public void assignDailyMissionToAllPartnerships() {

        // 1. 랜덤 미션 받아오기
        Optional<Mission> randomMissionOpt = missionRepository.findRandomMission();

        if (randomMissionOpt.isEmpty()) {
            log.info("할당할 미션이 DB에 존재하지 않아 스케줄러를 종료합니다.");
            return;
        }
        Mission selectedMission = randomMissionOpt.get();
        log.info("오늘의 미션으로 '{}'가 선택되었습니다.", selectedMission.getTitle());

        // 2. ACCEPTED 상태의 모든 파트너 조회
        List<Partnership> partners = partnershipRepository.findAllByStatus(PartnershipStatus.ACCEPTED);
        if(partners.isEmpty()) {
            log.info("미션을 할당할 파트너가 없어 스케줄러를 종료합니다.");
            return;
        }

        //데일리 미션을 담을 리스트
        List<DailyMission> dailyMissions = new ArrayList<>();

        //미션 할당 날자
        LocalDate today = LocalDate.now();

        // 3. DailyMission 리스트 생성
        for (Partnership partner : partners) {
            DailyMission todayMission = DailyMission.builder()
                    .partnership(partner)
                    .mission(selectedMission)
                    .missionDate(today)
                    .build();

            dailyMissions.add(todayMission);
        }

        // 4. 생성된 DailyMission들을 DB에 한 번에 저장.
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
        return new TodayMissionResponse(todayMission.getMission());
    }
}
