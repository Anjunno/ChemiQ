package com.chemiq.DTO;

import com.chemiq.entity.Partnership;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "받은 파트너 요청에 대한 응답DTO")
public class PartnershipReceiveResponse {

    private final Long partnershipId;
    private final String requesterId;
    private final String requesterNickname;

    public PartnershipReceiveResponse(Partnership partnership) {
        this.partnershipId = partnership.getId();
        this.requesterId = partnership.getRequester().getMemberId();
        this.requesterNickname = partnership.getRequester().getNickname();
    }

}