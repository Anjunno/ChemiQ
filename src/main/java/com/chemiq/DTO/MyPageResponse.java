package com.chemiq.DTO;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MyPageResponse {
    private MemberInfoDto myInfo;
    private MemberInfoDto partnerInfo; // 파트너가 없을 경우 null
    private PartnershipInfoDto partnershipInfo; // 파트너가 없을 경우 null
    private List<AchievementDto> myAchievements;
}