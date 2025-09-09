package com.chemiq.DTO;

import com.chemiq.entity.Submission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@AllArgsConstructor
@Schema(description = "사용자 타임라인에 대한 응답DTO")
public class TimelineResponse {
    private Long submissionId;
    private String missionTitle;
    private LocalDate missionDate;
    private String submitterNickname;
    private String imageUrl; //DB에 저장된 fileKey가 아닌, 최종 Pre-signed URL이 담길 필드
    private String content;
    private LocalDateTime createdAt;

    // Submission 엔티티와 Pre-signed URL을 받아 DTO를 생성
    public TimelineResponse(Submission submission, String presignedImageUrl) {
        this.submissionId = submission.getId();
        this.missionTitle = submission.getDailyMission().getMission().getTitle();
        this.missionDate = submission.getDailyMission().getMissionDate();
        this.submitterNickname = submission.getSubmitter().getNickname();
        this.imageUrl = presignedImageUrl;
        this.content = submission.getContent();
        this.createdAt = submission.getCreatedAt();
    }
}
