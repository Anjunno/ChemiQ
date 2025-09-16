package com.chemiq.DTO;

import com.chemiq.entity.MemberAchievement;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Data
public class AchievementDto {
    private String name;
    private String description;
    private LocalDateTime earnedAt; // 달성 시간

    public AchievementDto(MemberAchievement memberAchievement) {
        this.name = memberAchievement.getAchievement().getName();
        this.description = memberAchievement.getAchievement().getDescription();
        this.earnedAt = memberAchievement.getEarnedAt();
    }
}