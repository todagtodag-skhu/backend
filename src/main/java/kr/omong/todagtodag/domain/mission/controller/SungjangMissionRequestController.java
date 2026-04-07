package kr.omong.todagtodag.domain.mission.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.mission.service.MissionRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sungjang/mission-request")
@RequiredArgsConstructor
@Tag(name = "성장이 미션 조르기 API", description = "성장이의 미션 조르기 요청")
public class SungjangMissionRequestController {

    private final MissionRequestService missionRequestService;

    @Operation(
            summary = "미션 조르기",
            description =
                    """
                    성장이가 특정 미션에 대해 조르기 요청을 합니다.
                    
                    헤더에 성장이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 미션 id가 필요합니다.
                    
                    이미 조르기 요청이 존재할 경우 에러가 발생합니다.
                    
                    토닥이 유저가 이 API를 실행할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조르기 요청 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 성장이 유저로 요청하지 않음, 또는 해당 관계의 성장이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 미션이 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "이미 조르기 요청이 존재함")
    })
    @PostMapping("/{missionId}")
    public ResponseEntity<Void> requestMission(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long missionId
    ) {
        missionRequestService.requestMission(userId, missionId);
        return ResponseEntity.ok().build();
    }
}
