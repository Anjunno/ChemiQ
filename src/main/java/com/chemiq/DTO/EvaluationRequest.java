package com.chemiq.DTO;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationRequest {

    @Min(0)
    @Max(5)
    private double score; // 0 ~ 5 사이의 점수

    private String comment;
}