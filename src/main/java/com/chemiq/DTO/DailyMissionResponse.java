package com.chemiq.DTO;

import com.chemiq.entity.DailyMission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DailyMissionResponse {

    private final Long dailyMissionId;
    private final String missionTitle;
    private final LocalDate missionDate;
    private SubmissionDetailDto mySubmission; // 나의 제출물
    private SubmissionDetailDto partnerSubmission; // 파트너의 제출물
}