package com.emolink.emolink.DTO;

import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.entity.PartnershipStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "사용자가 요청한 파트너 요청에 대한 응답DTO")
public class PartnershipSentResponse {
    private final Long partnershipId;
    private final String addresseeId;
    private final String addresseeNickname;
    private final PartnershipStatus status;

    public PartnershipSentResponse(Partnership partnership) {
        this.partnershipId = partnership.getId();
        this.addresseeId = partnership.getAddressee().getMemberId();
        this.addresseeNickname = partnership.getAddressee().getNickname();
        this.status = partnership.getStatus();
    }
}
