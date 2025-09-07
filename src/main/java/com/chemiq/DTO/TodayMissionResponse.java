package com.chemiq.DTO;

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
    private final Long missionId;
    private final String title;
    private final String description;

    public TodayMissionResponse(Mission mission) {
        this.missionId = mission.getId();
        this.title = mission.getTitle();
        this.description = mission.getDescription();
    }
}
