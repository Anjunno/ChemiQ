package com.chemiq.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NicknameChangeRequest {
    @NotBlank
    @Size(min = 2, max = 6, message = "닉네임은 2자 이상 6자 이하로 입력해주세요.")
    private String nickname;
}
