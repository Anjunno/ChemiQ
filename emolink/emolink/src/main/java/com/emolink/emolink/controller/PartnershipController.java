package com.emolink.emolink.controller;

import com.emolink.emolink.DTO.*;
import com.emolink.emolink.entity.Partnership;
import com.emolink.emolink.exception.MemberNotFoundException;
import com.emolink.emolink.service.PartnershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "파트너십 (Partnership) API", description = "파트너 요청, 수락 등 커플 연결 관련 API")
public class PartnershipController {

    private final PartnershipService partnershipService;
    @Operation(
            summary = "파트너 요청 보내기",
            description = "로그인된 사용자가 다른 사용자에게 파트너 관계를 요청합니다.",
            security = @SecurityRequirement(name = "JWT"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "파트너로 요청할 사용자의 ID를 담은 JSON 객체",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PartnershipRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "파트너 요청 성공"),
                    @ApiResponse(responseCode = "404", description = "요청한 상대방 사용자를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 파트너 관계이거나 처리 대기중인 요청이 존재함",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 자기 자신에게 요청)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    //파트너 요청
    @PostMapping("/partnerships/requests")
    public ResponseEntity<?> createPartnershipRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                    @RequestBody PartnershipRequest partnershipRequest) {
        try{
            Long requesterNo = customUserDetails.getMemberNo();
            String addresseeId = partnershipRequest.getPartnerId();
            Partnership newPartnershipRequest = partnershipService.createRequest(requesterNo, addresseeId);

            // 성공 시: 201 Created 응답
            URI location = URI.create("/partnership/request/" + newPartnershipRequest.getId());
            return ResponseEntity.created(location).body("파트너 신청이 완료되었습니다.");

        } catch (MemberNotFoundException e) {
            // 실패 1: 상대방 사용자를 찾을 수 없음 (404 Not Found)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (IllegalStateException e) {
            // 실패 2: 이미 파트너가 있거나 요청이 존재함 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);

        } catch (IllegalArgumentException e) {
            // 실패 3: 자기 자신에게 요청하는 등 잘못된 인자로 요청 (400 Bad Request)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @Operation(
            summary = "파트너 관계 해제",
            description = "현재 로그인된 사용자의 파트너 관계를 해제합니다. 성공 시 관계의 상태는 'CANCELED'로 변경됩니다.",
            security = @SecurityRequirement(name = "JWT"), // 이 API가 JWT 인증을 필요로 함을 명시
            responses = {
                    @ApiResponse(responseCode = "200", description = "파트너 관계 해제 성공"),
                    @ApiResponse(responseCode = "409", description = "해제할 파트너 관계가 존재하지 않음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    @DeleteMapping("/partnerships")
    // 파트너 관계 해제
    public ResponseEntity<?> deletePartnership(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            Long memberNo = customUserDetails.getMemberNo();
            partnershipService.cancelPartnership(memberNo);

            return ResponseEntity.ok().body("파트너 관계가 해제되었습니다.");

        } catch (IllegalStateException e) {
            // 서비스에서 "파트너가 없다"는 예외를 던진 경우 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

    }


    @Operation(
            summary = "파트너 요청 수락",
            description = "로그인된 사용자가 자신에게 온 파트너 요청을 수락하여, 두 사용자 간의 파트너 관계를 최종적으로 형성합니다. 요청의 상태가 'PENDING'에서 'ACCEPTED'로 변경됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "파트너 요청 수락 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너십 요청 (잘못된 requestId)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "해당 요청을 수락할 권한이 없음 (요청의 수신자가 아님)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "요청을 수락할 수 없는 상태 (예: 이미 처리된 요청, 상대방이 다른 파트너와 연결됨)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    // 파트너 관계 수락
    @PostMapping("/partnerships/requests/{partnershipId}/accept")
    // 파트너 관계 수락
    public ResponseEntity<?> acceptPartnershipRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable Long partnershipId) {
        try{
            // 수락하는 주체(파트너 요청 받은 사람)
            Long addresseeNo = customUserDetails.getMemberNo();
            partnershipService.acceptPartnership(partnershipId, addresseeNo);

            return ResponseEntity.ok("파트너 요청을 수락했습니다.");
        } catch (EntityNotFoundException e) {
            // 실패 1: 해당 요청을 찾을 수 없음 (404 NOT_FOUND)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (AccessDeniedException e) {
            // 실패 2: 요청을 수락할 권한이 없음 (403 FORBIDDEN)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (IllegalStateException e) {
            // 실패 3: 이미 처리된 요청이거나, 누군가 이미 파트너가 있음 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

    }

    @Operation(
            summary = "파트너 요청 거절",
            description = "로그인된 사용자가 자신에게 온 파트너 요청을 거절합니다. 성공 시 관계의 상태는 'REJECTED'로 변경됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "파트너 요청 거절 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "파트너 요청을 거절했습니다."))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너십 요청 (잘못된 ID)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "해당 요청을 거절할 권한이 없음 (요청의 수신자가 아님)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 처리된 요청이라 거절할 수 없음 (예: 이미 수락됨)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/partnerships/requests/{partnershipId}/reject")
    //파트너 관계 거절
    public ResponseEntity<?> rejectPartnershipRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable Long partnershipId) {

        try{ // 수락하는 주체(파트너 요청 받은 사람)
            Long addresseeNo = customUserDetails.getMemberNo();
            partnershipService.rejectPartnership(partnershipId, addresseeNo);

            return ResponseEntity.ok("파트너 요청을 거절했습니다.");
        } catch (EntityNotFoundException e) {
            // 실패 1: 해당 요청을 찾을 수 없음 (404 NOT_FOUND)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (AccessDeniedException e) {
            // 실패 2: 요청을 거절할 권한이 없음 (403 FORBIDDEN)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (IllegalStateException e) {
            // 실패 3: 이미 처리된 요청이거나, 유효한 요청이 아님 (409 Conflict)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }

    @Operation(
            summary = "받은 파트너 요청 목록 조회",
            description = "현재 로그인한 사용자가 다른 사용자들로부터 받은, 'PENDING' 상태인 파트너 요청 목록을 조회합니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 목록 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    // 응답이 리스트(배열) 형태임을 명시합니다.
                                    array = @ArraySchema(schema = @Schema(implementation = PartnershipReceiveResponse.class)))),
            }
    )
    @GetMapping("/partnerships/requests/received")
    // 받은 파트너 요청 조회
    public ResponseEntity<?> getReceivedRequests(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<PartnershipReceiveResponse> recivedList = partnershipService.searchReciveList(customUserDetails.getMemberNo());

        return ResponseEntity.ok(recivedList);
    }


    @Operation(
            summary = "보낸 파트너 요청 목록 조회",
            description = "현재 로그인한 사용자가 다른 사용자들에게 요청한 파트너 요청 목록을 조회합니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 목록 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PartnershipSentResponse.class)))),
            }
    )
    @GetMapping("/partnerships/requests/sent")
    // 보낸 파트너 요청 조회
    public ResponseEntity<?> getSentRequests(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<PartnershipSentResponse> sentList = partnershipService.searchSentList(customUserDetails.getMemberNo());

        return ResponseEntity.ok(sentList);
    }


    @Operation(
            summary = "현재 파트너 정보 조회",
            description = "로그인된 사용자의 현재 파트너(ACCEPTED 상태) 정보를 조회합니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "파트너 정보 조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PartnershipPartnerResponse.class))),
                    @ApiResponse(responseCode = "404", description = "파트너 관계가 존재하지 않음",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    // 사용자의 파트너 정보를 받아옴
    @GetMapping("/partnerships")
    // 자신의 파트너 정보 조회
    public ResponseEntity<?> getPartnership(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try {

            PartnershipPartnerResponse partnerInfo = partnershipService.findPartnerInfo(customUserDetails.getMemberNo());
            return ResponseEntity.ok(partnerInfo);

        } catch (EntityNotFoundException e) {
            // 실패 1: 해당 요청을 찾을 수 없음 (404 NOT_FOUND)
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }


    @Operation(
            summary = "보낸 파트너 요청 취소",
            description = "로그인된 사용자가 보냈던 파트너 요청(PENDING 상태)을 취소합니다. 성공 시 관계의 상태는 'CANCELED'로 변경됩니다.",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 취소 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "파트너 요청이 취소되었습니다."))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 파트너십 요청 (잘못된 ID)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "해당 요청을 취소할 권한이 없음 (요청자가 아님)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "취소할 수 없는 상태의 요청 (예: 이미 수락됨)",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @DeleteMapping("/partnerships/requests/{partnershipId}/cancel")
    // 보낸 요청 취소
    public ResponseEntity<?> cancelPartnershipRequest(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @PathVariable Long partnershipId) {
        try {
            partnershipService.cancelRequest(partnershipId, customUserDetails.getMemberNo());
            return ResponseEntity.ok("파트너 요청이 취소되었습니다.");

        } catch (EntityNotFoundException e) {
            // 1. 요청 ID가 잘못된 경우
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

        } catch (AccessDeniedException e) {
            // 2. 내 요청이 아닌 경우
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);

        } catch (IllegalStateException e) {
            // 3. 이미 처리된 요청인 경우
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }
    }
}
