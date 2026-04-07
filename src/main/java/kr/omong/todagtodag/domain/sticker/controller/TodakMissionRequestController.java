package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.sticker.dto.MissionRequestListGetResponse;
import kr.omong.todagtodag.domain.sticker.service.MissionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todak/mission-request")
@RequiredArgsConstructor
@Tag(name = "토닥이 미션 조르기 API", description = "토닥이의 미션 조르기 요청 조회, 수락, 거절")
public class TodakMissionRequestController {

    private final MissionRequestService missionRequestService;

    @Operation(
            summary = "조르기 요청 목록 조회",
            description =
                    """
                    토닥이가 조르기 요청 목록을 조회합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 관계 id가 필요합니다.
                    
                    성장이 유저가 이 API를 실행할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조르기 요청 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저, 관계 또는 스티커판이 존재하지 않음")
    })
    @GetMapping("/{relationId}")
    public ResponseEntity<MissionRequestListGetResponse> getMissionRequests(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId
    ) {
        return ResponseEntity.ok(missionRequestService.getMissionRequests(userId, relationId));
    }

    @Operation(
            summary = "조르기 요청 수락",
            description =
                    """
                    토닥이가 조르기 요청을 수락합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 조르기 요청 id가 필요합니다.
                    
                    수락 시 성장이에게 스티커가 자동 지급됩니다.
                    
                    성장이 유저가 이 API를 실행할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조르기 요청 수락 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 조르기 요청이 존재하지 않음")
    })
    @PostMapping("/{missionRequestId}/accept")
    public ResponseEntity<Void> acceptMissionRequest(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionRequestId
    ) {
        missionRequestService.acceptMissionRequest(userId, missionRequestId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "조르기 요청 거절",
            description =
                    """
                    토닥이가 조르기 요청을 거절합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 조르기 요청 id가 필요합니다.
                    
                    거절 시 성장이는 다시 조르기 요청을 할 수 있습니다.
                    
                    성장이 유저가 이 API를 실행할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조르기 요청 거절 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 조르기 요청이 존재하지 않음")
    })
    @PostMapping("/{missionRequestId}/reject")
    public ResponseEntity<Void> rejectMissionRequest(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionRequestId
    ) {
        missionRequestService.rejectMissionRequest(userId, missionRequestId);
        return ResponseEntity.ok().build();
    }
}
