package com.chemiq.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class MemberSignUpRequest {

    @Schema(description = "회원 아이디")
    private String memberId;

    @Schema(description = "회원 비밀번호")
    private String password;

    @Schema(description = "회원 닉네임")
    private String nickname;

}
