package com.chemiq.DTO;

import com.chemiq.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "사용자의 파트너 정보 요청에 대한 응답DTO")
public class PartnershipPartnerResponse {
//    private final Long partnershipId;
    private final String partnerId;
    private final String partnerNickname;

    public PartnershipPartnerResponse(Member partner) {
//        this.partnershipId = partnership.getId();
        this.partnerId = partner.getMemberId();
        this.partnerNickname = partner.getNickname();
    }
}
