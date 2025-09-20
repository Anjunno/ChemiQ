package com.chemiq.service;

import com.chemiq.DTO.EvaluationRequest;
import com.chemiq.DTO.EvaluationResponse;
import com.chemiq.entity.*;
import com.chemiq.repository.EvaluationRepository;
import com.chemiq.repository.MemberRepository;
import com.chemiq.repository.PartnershipRepository;
import com.chemiq.repository.SubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final PartnershipRepository partnershipRepository;
    private final SubmissionRepository submissionRepository;
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    @Transactional
    public Evaluation createEvaluation(Long memberNo, Long submissionId, EvaluationRequest requestDto) {

        // 1. 점수 유효성 검증
        double score = requestDto.getScore();
        if (score < 0 || score > 5 || (score * 10) % 5 != 0) {
            throw new IllegalArgumentException("점수는 0에서 5 사이의 0.5 단위 값이어야 합니다.");
        }

        // 2. 평가할 대상(Submission) 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + submissionId + "에 해당하는 제출물을 찾을 수 없습니다."));

        Member evaluator = memberRepository.getReferenceById(memberNo);
        Member submitter = submission.getSubmitter();

        // 3. 자기 자신에게 평가하는지 확인
        if (evaluator.getMemberNo().equals(submitter.getMemberNo())) {
            throw new AccessDeniedException("자신의 제출물에는 평가를 남길 수 없습니다.");
        }

        // 4. 평가자와 제출자가 'ACCEPTED' 상태의 파트너가 맞는지 DB에서 직접 확인
        boolean arePartners = partnershipRepository.existsAcceptedPartnershipBetween(evaluator, submitter);

        if (!arePartners) {
            throw new AccessDeniedException("파트너의 제출물에만 평가를 남길 수 있습니다.");
        }

        // 5. 중복 평가 방지
        if (evaluationRepository.existsBySubmission(submission)) {
            throw new IllegalStateException("이미 평가가 완료된 제출물입니다.");
        }

        // 6. 모든 검증 통과 후, Evaluation 엔티티 생성 및 저장
        Evaluation evaluation = Evaluation.builder()
                .evaluator(evaluator)
                .submission(submission)
                .score(score)
                .comment(requestDto.getComment())
                .build();

        evaluationRepository.save(evaluation);

        // 미션 최종 완료 및 점수 업데이트 로직
        checkAndCompleteDailyMission(evaluation.getSubmission().getDailyMission());


        return evaluation;
    }


    private void checkAndCompleteDailyMission(DailyMission dailyMission) {
        // 1. 해당 DailyMission에 속한 Submission 목록을 가져옴.
        List<Submission> submissions = submissionRepository.findAllByDailyMission(dailyMission);

        // 2. 두 파트너가 모두 제출했는지 확인.
        if (submissions.size() < 2) {
            return; // 아직 둘 다 제출하지 않았으므로 로직 종료
        }

        // 3. 두 제출물에 대한 평가가 모두 완료되었는지 확인.
        List<Evaluation> evaluations = evaluationRepository.findAllBySubmissionIn(submissions);
        if (evaluations.size() < 2) {
            return; // 아직 둘 다 평가하지 않았으므로 로직 종료
        }

        // 4. 모든 조건이 충족되었으므로, 미션 최종 완료 처리.

        // 미션의 상태를 COMPLETED로 변경
        dailyMission.setStatus(DailyMissionStatus.COMPLETED);

        Partnership partnership = dailyMission.getPartnership();
        // 4-1. 스트릭(streak) 1 증가.
        partnership.increaseStreak();

        // 4-2. 케미 지수(chemiScore)를 업데이트.(+0.2)
        partnership.increaseChemiScoreByCompletion();

    }

    @Transactional(readOnly = true)
    public EvaluationResponse getEvaluation(Long submissionId, Long memberNo) {

        // 1. submissionId로 Submission 엔티티 조회
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new EntityNotFoundException("ID " + submissionId + "에 해당하는 제출물을 찾을 수 없습니다."));

        // 2. 로그인한 사용자가 해당 제출물과 관련된 파트너십의 멤버가 맞는지 확인
        Partnership partnership = submission.getDailyMission().getPartnership();
        if (!partnership.getRequester().getMemberNo().equals(memberNo) &&
                !partnership.getAddressee().getMemberNo().equals(memberNo)) {
            throw new AccessDeniedException("해당 평가를 조회할 권한이 없습니다.");
        }

        // 3. Submission에 해당하는 Evaluation 조회
        Evaluation evaluation = evaluationRepository.findBySubmissionWithEvaluator(submission)
                .orElseThrow(() -> new EntityNotFoundException("아직 평가가 등록되지 않았습니다."));

        Member member = evaluation.getEvaluator();
        String url = s3Service.getDownloadPresignedUrl(member.getProfileImageKey());

        // 4. Entity를 DTO로 변환하여 반환
        return new EvaluationResponse(evaluation, url);
    }
}
