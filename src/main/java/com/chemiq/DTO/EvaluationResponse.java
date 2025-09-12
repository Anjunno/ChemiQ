package com.chemiq.DTO;

import com.chemiq.entity.Evaluation;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EvaluationResponse {

    private final double score;
    private final String comment;
    private final String evaluatorNickname; // 누가 평가했는지
    private final LocalDateTime createdAt;

    public EvaluationResponse(Evaluation evaluation) {
        this.score = evaluation.getScore();
        this.comment = evaluation.getComment();
        this.evaluatorNickname = evaluation.getEvaluator().getNickname();
        this.createdAt = evaluation.getCreatedAt();
    }
}