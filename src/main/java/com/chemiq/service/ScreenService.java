package com.chemiq.service;

import com.chemiq.DTO.*;
import com.chemiq.entity.DailyMissionStatus;
import com.chemiq.entity.Member;
import com.chemiq.entity.MemberAchievement;
import com.chemiq.entity.Partnership;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.MemberAchievementRepository;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScreenService {

    private final MemberRepository memberRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final PartnershipRepository partnershipRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final S3Service s3Service;
    private final MemberService memberService;
    private final MissionService missionService;
    private final TimelineService timelineService;
    private final PartnershipService partnershipService;


    @Transactional(readOnly = true)
    public MyPageResponse getMyPageInfo(Long memberNo) {

        // 1. 내 정보 조회
        Member me = memberRepository.findById(memberNo)
                .orElseThrow(() -> new EntityNotFoundException(memberNo + "에 해당하는 사용자를 찾을 수 없습니다"));

        // 내 프로필 이미지에 대한 Pre-signed URL 생성
        String myProfileImageUrl = s3Service.getDownloadPresignedUrl(me.getProfileImageKey());
        // 수정된 생성자를 사용하여 DTO 생성
        MemberInfoDto myInfoDto = new MemberInfoDto(me, myProfileImageUrl);

        // 내 도전과제 목록 조회 및 DTO 변환
        List<MemberAchievement> achievements = memberAchievementRepository.findAllByMemberWithAchievement(me);
        List<AchievementDto> achievementDtos = achievements.stream()
                .map(AchievementDto::new)
                .collect(Collectors.toList());

        // 2. 파트너십 정보 조회
        Optional<Partnership> partnershipOpt = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo);

        // 3. 파트너가 있는 경우와 없는 경우를 분기하여 DTO 생성
        if (partnershipOpt.isPresent()) {
            // --- 파트너가 있는 경우 ---
            Partnership partnership = partnershipOpt.get();
            Member partner = me.getMemberNo().equals(partnership.getRequester().getMemberNo())
                    ? partnership.getAddressee()
                    : partnership.getRequester();

            //  파트너 프로필 이미지에 대한 Pre-signed URL 생성
            String partnerProfileImageUrl = s3Service.getDownloadPresignedUrl(partner.getProfileImageKey());

            // 수정된 생성자를 사용하여 DTO 생성
            MemberInfoDto partnerInfoDto = new MemberInfoDto(partner, partnerProfileImageUrl);

            long totalMissions = dailyMissionRepository.countByPartnershipAndStatus(
                    partnership, DailyMissionStatus.COMPLETED);

            // 2. 이번 주 완료 미션 개수 조회
            LocalDate today = LocalDate.now();
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            long weeklyMissions = dailyMissionRepository.countByPartnershipAndStatusAndMissionDateBetween(
                    partnership, DailyMissionStatus.COMPLETED, startOfWeek, endOfWeek);

            // 수정된 생성자로 PartnershipInfoDto 생성
            PartnershipInfoDto partnershipInfoDto = new PartnershipInfoDto(partnership, totalMissions, weeklyMissions);

            return MyPageResponse.builder()
                    .myInfo(myInfoDto)
                    .partnerInfo(partnerInfoDto)
                    .partnershipInfo(partnershipInfoDto)
                    .myAchievements(achievementDtos)
                    .build();
        } else {
            // --- 파트너가 없는 경우 ---
            return MyPageResponse.builder()
                    .myInfo(myInfoDto)
                    .myAchievements(achievementDtos)
                    .build();
        }
    }

    @Transactional
    public HomeSummaryResponse getHomeScreenSummary(Long memberNo) {



        HomePartnerInfoDto partnerInfoDto = null;
        WeeklyMissionStatusResponse weeklyStatusDto = null;
        DailyMissionResponse dailyMissionDto = null;

        // 1. 파트너가 있는지 먼저 확인합니다.
        Optional<HomePartnerInfoDto> partnerInfoOpt = partnershipService.getHomePartnerInfo(memberNo);

        // 2. 파트너가 있는 경우에만 나머지 정보들을 조회합니다.
        if (partnerInfoOpt.isPresent()) {

             partnerInfoDto = partnerInfoOpt.get();

            // 주간 미션 현황 조회 로직 호출
            weeklyStatusDto = missionService.getWeeklyMissionStatus(memberNo);

            // 오늘의 미션 현황 조회 로직 호출
            dailyMissionDto = timelineService.getTodayMissionStatus(memberNo);
        }

        // 3. 모든 정보를 종합하여 최종 DTO를 생성하여 반환합니다.
        return HomeSummaryResponse.builder()
                .partnerInfo(partnerInfoDto)
                .weeklyStatus(weeklyStatusDto)
                .dailyMission(dailyMissionDto)
                .build();
    }
}
