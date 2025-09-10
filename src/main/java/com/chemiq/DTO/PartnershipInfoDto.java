package com.chemiq.DTO;

import com.chemiq.entity.Partnership;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PartnershipInfoDto {

    private Integer streakCount;
    private Double chemiScore;
    private LocalDate acceptedAt;

    public PartnershipInfoDto(Partnership partnership) {
        this.streakCount = partnership.getStreakCount();
        this.chemiScore = partnership.getChemiScore();
        this.acceptedAt = partnership.getAcceptedAt();
    }

}
