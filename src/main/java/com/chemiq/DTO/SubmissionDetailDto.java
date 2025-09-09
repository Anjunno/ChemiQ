package com.chemiq.DTO;

import com.chemiq.entity.Submission;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
//개별 제출물 정보 DTO
public class SubmissionDetailDto {

    private final Long submissionId;
    private final String imageUrl;
    private final String content;
    private final LocalDateTime createdAt;

    public SubmissionDetailDto(Submission submission, String presignedImageUrl) {
        this.submissionId = submission.getId();
        this.imageUrl = presignedImageUrl;
        this.content = submission.getContent();
        this.createdAt = submission.getCreatedAt();
    }
}