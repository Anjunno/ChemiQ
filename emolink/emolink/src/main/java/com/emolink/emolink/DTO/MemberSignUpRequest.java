package com.emolink.emolink.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MemberSignUpRequest {

    private String memberId;

    private String password;

    private String nickname;

}
