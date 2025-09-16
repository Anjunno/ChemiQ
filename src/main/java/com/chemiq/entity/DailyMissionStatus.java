package com.chemiq.entity;

public enum DailyMissionStatus {
    ASSIGNED,  // 할당됨 (미션 진행 중)
    COMPLETED,  // 양쪽 모두 제출 및 평가까지 완료함
    FAILED,     // 하루가 지나도록 완료하지 못함
    NOT_ASSIGNED // 미션이 할당되지 않음
}
