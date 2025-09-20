package com.chemiq.DTO;

import com.chemiq.entity.Partnership;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class HomePartnerInfoDto {
    private String nickname;
    private String profileImageUrl;
    private Integer streakCount;
    private double chemiScore;


    public HomePartnerInfoDto(Partnership partnership, String nickname,String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.streakCount = partnership.getStreakCount();
        this.chemiScore = partnership.getChemiScore();;
    }


}
