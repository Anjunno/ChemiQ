package com.chemiq.DTO;

import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Mission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "사용자가 요청한 오늘의 미션에 대한 응답DTO")
public class TodayMissionResponse {
    private final Long dailyMissionId; // 오늘의 미션 고유 ID
    private final Long missionId;
    private final String title;
    private final String description;

    public TodayMissionResponse(DailyMission dailyMission) {
        this.dailyMissionId = dailyMission.getId(); // ◀◀ DailyMission의 ID를 할당
        this.missionId = dailyMission.getMission().getId();
        this.title = dailyMission.getMission().getTitle();
        this.description = dailyMission.getMission().getDescription();
    }
}
