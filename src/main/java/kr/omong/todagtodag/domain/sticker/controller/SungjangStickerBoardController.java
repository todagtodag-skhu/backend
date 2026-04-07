package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardListMemoryResponse;
import kr.omong.todagtodag.domain.sticker.service.StickerBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sungjang/sticker-board")
@RequiredArgsConstructor
@Tag(name = "성장이 스티커판 API", description = "성장이의 스티커판 조회 및 관리")
public class SungjangStickerBoardController {

    private final StickerBoardService stickerBoardService;

    @Operation(
            summary = "활성화 스티커판 조회",
            description =
                    """
                    성장이 유저가 현재 활성화된 스티커판을 조회합니다.
                    
                    헤더에 성장이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 해당 관계의 id가 필요합니다.
                    
                    활성화된 스티커판 정보와 해당 스티커판의 부착된 모든 스티커 정보를 반환합니다.
                    
                    토닥이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 성장이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커판 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 성장이 유저로 요청하지 않음, 또는 해당 관계의 성장이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @GetMapping
    public ResponseEntity<StickerBoardGetResponse> getStickerBoard(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(stickerBoardService.getStickerBoard(userId));
    }

    @Operation(
            summary = "추억 저장소 조회",
            description =
                    """
                    성장이 유저가 완성된 스티커판 목록을 조회합니다.
                    
                    헤더에 성장이 유저의 accessToken을 담아 호출해야 합니다.
                    
                    완성된 스티커판의 타이틀, 스티커 세부 내용, 보상 내용을 반환합니다.
                    
                    토닥이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추억 저장소 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 성장이 유저로 요청하지 않음"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @GetMapping("/memory")
    public ResponseEntity<StickerBoardListMemoryResponse> getCompletedStickerBoards(
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(stickerBoardService.getCompletedStickerBoards(userId));
    }
}
