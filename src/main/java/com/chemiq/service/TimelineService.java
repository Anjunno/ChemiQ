package com.chemiq.service;

import com.chemiq.DTO.DailyMissionResponse;
import com.chemiq.DTO.SubmissionDetailDto;
import com.chemiq.DTO.TimelineResponse;
import com.chemiq.entity.DailyMission;
import com.chemiq.entity.Evaluation;
import com.chemiq.entity.Partnership;
import com.chemiq.entity.Submission;
import com.chemiq.repository.DailyMissionRepository;
import com.chemiq.repository.EvaluationRepository;
import com.chemiq.repository.PartnershipRepository;
import com.chemiq.repository.SubmissionRepository;
import com.chemiq.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
@RequiredArgsConstructor
public class TimelineService {

    private final PartnershipRepository partnershipRepository;
    private final SubmissionRepository submissionRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final EvaluationRepository evaluationRepository;
    private final S3Service s3Service; // 이미지 조회를 위한 Pre-signed URL 생성에 필요


    @Transactional(readOnly = true)
    public Page<DailyMissionResponse> getTimeline(Long memberNo, int page, int size) {

        // 1. 파트너십 조회 (기존과 동일)
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElse(null);
        if (partnership == null) {
            return Page.empty();
        }

        // 2. 'DailyMission' 페이징 조회 (기존과 동일)
        Pageable pageable = PageRequest.of(page, size, Sort.by("missionDate").descending());
        Page<DailyMission> dailyMissionPage = dailyMissionRepository.findByPartnershipOrderByMissionDateDesc(partnership, pageable);

        // 3. 'Submission' 목록 한번에 조회 (기존과 동일)
        List<Submission> submissions = submissionRepository.findAllByDailyMissionIn(dailyMissionPage.getContent());

        // 4. [추가] 조회된 Submission들에 대한 'Evaluation' 목록을 한번에 조회
        List<Evaluation> evaluations = evaluationRepository.findAllBySubmissionIn(submissions);

        // 5. [수정] Submission과 Evaluation을 쉽게 찾을 수 있도록 Map으로 변환
        Map<Long, List<Submission>> submissionsMap = submissions.stream()
                .collect(Collectors.groupingBy(s -> s.getDailyMission().getId()));

        Map<Long, Evaluation> evaluationMap = evaluations.stream()
                .collect(Collectors.toMap(e -> e.getSubmission().getId(), e -> e));


        // 6. Page<DailyMission>을 Page<DailyMissionResponseDto>로 변환
        return dailyMissionPage.map(dailyMission -> {
            List<Submission> missionSubmissions = submissionsMap.getOrDefault(dailyMission.getId(), Collections.emptyList());

            SubmissionDetailDto mySubmissionDto = null;
            SubmissionDetailDto partnerSubmissionDto = null;

            for (Submission s : missionSubmissions) {
                String presignedUrl = s3Service.getDownloadPresignedUrl(s.getImageUrl());

                // Map에서 해당 제출물에 대한 평가 정보를 찾음. 없으면 null.
                Evaluation evaluation = evaluationMap.get(s.getId());
                Double score = (evaluation != null) ? evaluation.getScore() : null;

                if (s.getSubmitter().getMemberNo().equals(memberNo)) {
                    mySubmissionDto = new SubmissionDetailDto(s, presignedUrl, score); // score 전달
                } else {
                    partnerSubmissionDto = new SubmissionDetailDto(s, presignedUrl, score); // score 전달
                }
            }

            // 최종적으로 하루치 미션 묶음 DTO를 생성하여 반환
            return DailyMissionResponse.builder()
                    .dailyMissionId(dailyMission.getId())
                    .missionTitle(dailyMission.getMission().getTitle())
                    .missionDate(dailyMission.getMissionDate())
                    .mySubmission(mySubmissionDto)
                    .partnerSubmission(partnerSubmissionDto)
                    .build();
        });
    }

    @Transactional(readOnly = true)
    public DailyMissionResponse getTodayMissionStatus(Long memberNo) {

        // 1. 파트너 존재여부 확인 (기존과 동일)
        Partnership partnership = partnershipRepository.findAcceptedPartnershipByMemberNo(memberNo)
                .orElseThrow(() -> new EntityNotFoundException("파트너가 존재하지 않습니다."));

        // 2. 해당 파트너십의 오늘의 미션 조회 (기존과 동일)
        LocalDate today = LocalDate.now();
        DailyMission dailyMission = dailyMissionRepository.findByPartnershipAndMissionDateWithMission(partnership, today)
                .orElseThrow(() -> new EntityNotFoundException("오늘 할당된 미션이 없습니다."));

        // 3. 해당 미션에 대한 제출물들 조회 (기존과 동일)
        List<Submission> todaySubmissions = submissionRepository.findAllByDailyMissionWithSubmitter(dailyMission);

        // --- [추가] 제출물들에 대한 평가 정보 조회 ---
        // 3-1. 조회된 제출물들에 대한 모든 평가 기록을 '한 번의 쿼리'로 가져옵니다.
        List<Evaluation> evaluations = evaluationRepository.findAllBySubmissionIn(todaySubmissions);

        // 3-2. Submission ID를 Key로, Evaluation 객체를 Value로 갖는 Map을 만들어 점수를 쉽게 찾도록 합니다.
        Map<Long, Evaluation> evaluationMap = evaluations.stream()
                .collect(Collectors.toMap(e -> e.getSubmission().getId(), e -> e));

        SubmissionDetailDto mySubmissionDto = null;
        SubmissionDetailDto partnerSubmissionDto = null;

        // 4. 제출물을 '나'와 '파트너'로 구분하여 DTO 생성
        for (Submission s : todaySubmissions) {
            String presignedUrl = s3Service.getDownloadPresignedUrl(s.getImageUrl());

            // [수정] Map에서 해당 제출물에 대한 평가 정보를 찾습니다. 없으면 null.
            Evaluation evaluation = evaluationMap.get(s.getId());
            Double score = (evaluation != null) ? evaluation.getScore() : null;

            if (s.getSubmitter().getMemberNo().equals(memberNo)) {
                mySubmissionDto = new SubmissionDetailDto(s, presignedUrl, score); // ◀◀ score 전달
            } else {
                partnerSubmissionDto = new SubmissionDetailDto(s, presignedUrl, score); // ◀◀ score 전달
            }
        }

        // 5. 최종 응답 DTO 생성 후 반환
        return DailyMissionResponse.builder()
                .dailyMissionId(dailyMission.getId())
                .missionTitle(dailyMission.getMission().getTitle())
                .missionDate(dailyMission.getMissionDate())
                .mySubmission(mySubmissionDto)
                .partnerSubmission(partnerSubmissionDto)
                .build();
    }
}
