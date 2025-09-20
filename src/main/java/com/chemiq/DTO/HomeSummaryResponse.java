package com.chemiq.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class HomeSummaryResponse {

    private HomePartnerInfoDto partnerInfo; // 파트너 정보 (파트너 없으면 null)
    private WeeklyMissionStatusResponse weeklyStatus; // 주간 미션 현황 (파트너 없으면 null)
    private DailyMissionResponse dailyMission; // 오늘의 미션 (파트너 없으면 null)
}