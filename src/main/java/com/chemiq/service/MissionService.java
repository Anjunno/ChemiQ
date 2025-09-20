package com.chemiq.service;

import com.chemiq.DTO.MissionStatusDto;
import com.chemiq.DTO.TodayMissionResponse;
import com.chemiq.DTO.WeeklyMissionStatusResponse;
import com.chemiq.entity.*;
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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

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

            // 각 파트너십의 '어제' DailyMission을 찾습니다.
            Optional<DailyMission> yesterdayMissionOpt = dailyMissionRepository.findByPartnershipAndMissionDate(partnership, yesterday);

            // 어제 할당된 미션이 '있는' 경우에만 검사를 진행합니다.
            yesterdayMissionOpt.ifPresent(yesterdayMission -> {

                // 어제 미션의 상태가 여전히 'ASSIGNED'(진행중)인지 확인합니다.
                // 이렇게 하면 이미 COMPLETED 또는 FAILED로 처리된 미션은 건너뛰게 되어 더 안전합니다.
                if (yesterdayMission.getStatus() == DailyMissionStatus.ASSIGNED) {

                    // 이 시점에서 미션이 완료되지 않았음은 확실합니다.
                    // (만약 완료되었다면 EvaluationService에서 이미 COMPLETED로 상태를 바꿨을 것이므로)

                    log.info("파트너십 ID {}: 어제 미션 미완료. 스트릭 초기화 및 상태 변경.", partnership.getId());

                    // 1. 스트릭을 0으로 리셋합니다.
                    partnership.resetStreak();

                    // 2. 케미 지수 패널티를 적용합니다.
                    partnership.applyScorePenalty(0.1); // 0.1점 차감
                    log.info("파트너십 ID {}: 케미 지수 패널티 적용됨. 현재 점수: {}", partnership.getId(), partnership.getChemiScore());

                    // 3. DailyMission의 상태를 FAILED로 변경합니다.
                    yesterdayMission.setStatus(DailyMissionStatus.FAILED);
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
    @Transactional
    public TodayMissionResponse getTodayMission(Long memberNo) {

        // 1. 파트너 존재여부 확인
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("파트너가 존재하지 않습니다."));


        // 2. 해당 파트너십의 오늘의 미션
        LocalDate today = LocalDate.now();
        Optional<DailyMission> dailyMissionOpt = dailyMissionRepository.findByPartnershipAndMissionDateWithMission(partnership, today);

        DailyMission dailyMission;

        if (dailyMissionOpt.isPresent()) {
            // 2-1. 오늘의 미션이 이미 존재하면, 그대로 사용.
            dailyMission = dailyMissionOpt.get();
        } else {
            // 2-2. 오늘의 미션이 없다면 (자정 이후 커플이 됨), 즉시 새로 생성.
            log.info("파트너십 ID {}: 오늘 할당된 미션이 없어 새로 생성합니다.", partnership.getId());

            // 랜덤 미션 하나를 가져옵니다.
            Mission randomMission = missionRepository.findRandomMission()
                    .orElseThrow(() -> new EntityNotFoundException("할당할 미션 원본이 없습니다."));

            // 새로운 DailyMission을 만들어 DB에 저장합니다.
            dailyMission = DailyMission.builder()
                    .partnership(partnership)
                    .mission(randomMission)
                    .missionDate(today)
                    .build();
            dailyMissionRepository.save(dailyMission);
        }
        // 3. 미션 내용 반환
        return new TodayMissionResponse(dailyMission);
    }

    @Transactional(readOnly = true)
    public WeeklyMissionStatusResponse getWeeklyMissionStatus(Long memberNo) {

        // 1. 파트너십 조회
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("파트너가 존재하지 않습니다."));

        // 2. 이번 주의 시작일(월요일)과 종료일(일요일) 계산
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 3. DB에서 이번 주에 해당하는 DailyMission 목록을 한 번에 조회
        List<DailyMission> weekMissions = dailyMissionRepository
                .findAllByPartnershipAndDateRangeWithMission(partnership, startOfWeek, endOfWeek);

        // 4. 조회를 쉽게 하기 위해 날짜를 Key로 갖는 Map으로 변환
        Map<LocalDate, DailyMission> missionsByDate = weekMissions.stream()
                .collect(Collectors.toMap(DailyMission::getMissionDate, dm -> dm));

        // 5. 최종 결과를 담을 EnumMap 생성 (요일 순서 보장)
        Map<DayOfWeek, MissionStatusDto> weeklyStatusMap = new EnumMap<>(DayOfWeek.class);

        // 6. 월요일부터 일요일까지 순회하며 결과 Map을 채움
        for (LocalDate date = startOfWeek; !date.isAfter(endOfWeek); date = date.plusDays(1)) {
            DailyMission dailyMission = missionsByDate.get(date);
            MissionStatusDto statusDto;

            if (dailyMission != null) {
                // 해당 날짜에 할당된 미션이 있는 경우
                statusDto = MissionStatusDto.builder()
                        .dailyMissionId(dailyMission.getId())
                        .missionTitle(dailyMission.getMission().getTitle())
                        .status(dailyMission.getStatus())
                        .build();
            } else {
                // 해당 날짜에 할당된 미션이 없는 경우
                statusDto = MissionStatusDto.builder()
                        .status(DailyMissionStatus.NOT_ASSIGNED)
                        .build();
            }
            weeklyStatusMap.put(date.getDayOfWeek(), statusDto);
        }

        return new WeeklyMissionStatusResponse(weeklyStatusMap);
    }



    // 미션 완료 여부를 확인하는 헬퍼(helper) 메소드
    private boolean checkMissionCompletion(DailyMission dailyMission) {
        long evaluationCount = evaluationRepository.countByDailyMission(dailyMission);
        return evaluationCount >= 2;
    }
}
