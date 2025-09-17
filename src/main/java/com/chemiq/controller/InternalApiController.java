package com.chemiq.controller;

import com.chemiq.DTO.ImageKeyUpdateRequestDto;
import com.chemiq.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalApiController {

    private final SubmissionService submissionService;

    @Value("${internal.api.key}")
    private String internalApiKey;

    @PutMapping("/submissions/image-key")
    public ResponseEntity<String> updateSubmissionImageKey(
            @RequestHeader("X-Internal-Api-Key") String apiKey,
            @RequestBody ImageKeyUpdateRequestDto requestDto) {

        // 1. 헤더에 담겨온 API 키가 서버에 저장된 키와 일치하는지 확인.
        if (!internalApiKey.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }

        // 2. 서비스 로직을 호출하여 DB를 업데이트.
        submissionService.updateImageKey(requestDto.getOriginalFileKey(), requestDto.getNewFileKey());

        return ResponseEntity.ok("변환파일 업데이트 완료.");
    }
}