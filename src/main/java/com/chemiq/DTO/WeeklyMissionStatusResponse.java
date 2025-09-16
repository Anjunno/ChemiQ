package com.chemiq.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Map;

@Getter
@AllArgsConstructor
public class WeeklyMissionStatusResponse {
    private Map<DayOfWeek, MissionStatusDto > weeklyStatus; // 월~일요일까지의 상태 맵
}
