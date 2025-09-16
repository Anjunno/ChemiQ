package com.chemiq.DTO;

import com.chemiq.entity.DailyMissionStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionStatusDto {
    private Long dailyMissionId; // 미션을 수행해야 할 때 필요
    private String missionTitle;
    private DailyMissionStatus status; // COMPLETED, FAILED, ASSIGNED, NOT_ASSIGNED 등
}