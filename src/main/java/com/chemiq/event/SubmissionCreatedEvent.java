package com.chemiq.event;

import com.chemiq.entity.Submission;
import lombok.Getter;

@Getter
public class SubmissionCreatedEvent {

    private final Submission submission;

    public SubmissionCreatedEvent(Submission submission) {
        this.submission = submission;
    }
}