package com.chemiq.DTO;

import com.chemiq.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberInfoDto {
    private final String memberId;
    private final String nickname;
    private final LocalDate created;

    public MemberInfoDto(Member member) {
        this.memberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.created = LocalDate.from(member.getCreated());
    }
}
