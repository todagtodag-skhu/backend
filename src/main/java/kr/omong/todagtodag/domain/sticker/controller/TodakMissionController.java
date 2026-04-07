package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.sticker.dto.MissionCreateRequest;
import kr.omong.todagtodag.domain.sticker.dto.MissionCreateResponse;
import kr.omong.todagtodag.domain.sticker.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todak/mission")
@RequiredArgsConstructor
@Tag(name = "토닥이 미션 API", description = "토닥이의 미션 생성, 수정, 삭제, 성공 처리")
public class TodakMissionController {

    private final MissionService missionService;

    @Operation(
            summary = "미션 생성",
            description =
                    """
                    토닥이가 스티커판에 미션을 생성합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 관계 id가 필요합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 생성 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @PostMapping("/{relationId}")
    public ResponseEntity<MissionCreateResponse> createMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId,
            @RequestBody MissionCreateRequest request
    ) {
        Long missionId = missionService.createMission(userId, relationId, request);
        return ResponseEntity.ok(new MissionCreateResponse(missionId));
    }

    @Operation(
            summary = "미션 수정",
            description =
                    """
                    토닥이가 미션을 수정합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 미션 id가 필요합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 수정 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 미션이 존재하지 않음")
    })
    @PatchMapping("/{missionId}")
    public ResponseEntity<Void> updateMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionId,
            @RequestBody MissionCreateRequest request
    ) {
        missionService.updateMission(userId, missionId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "미션 삭제",
            description =
                    """
                    토닥이가 미션을 삭제합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 미션 id가 필요합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 미션이 존재하지 않음")
    })
    @DeleteMapping("/{missionId}")
    public ResponseEntity<Void> deleteMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionId
    ) {
        missionService.deleteMission(userId, missionId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "미션 성공 처리",
            description =
                    """
                    토닥이가 미션을 성공 처리합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 미션 id가 필요합니다.
                    
                    미션 성공 시 성장이에게 스티커가 자동 지급됩니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 성공 처리 완료"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 미션이 존재하지 않음")
    })
    @PostMapping("/{missionId}/complete")
    public ResponseEntity<Void> completeMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionId
    ) {
        missionService.completeMission(userId, missionId);
        return ResponseEntity.ok().build();
    }
}
