package com.chemiq.DTO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Schema(description = "파트터 요청 DTO")
public class PartnershipRequest {
    private String partnerId;
}
