package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardCreateRequest;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardCreateResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardListMemoryResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardTodakGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardUpdateRequest;
import kr.omong.todagtodag.domain.sticker.service.StickerBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todak/sticker-board")
@RequiredArgsConstructor
@Tag(name = "토닥이 스티커판 API", description = "토닥이의 스티커판 생성 및 관리")
public class TodakStickerBoardController {

    private final StickerBoardService stickerBoardService;

    @Operation(
            summary = "스티커판 생성",
            description =
                    """
                    토닥이 유저가 스티커판을 생성합니다.

                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 해당 관계의 id가 필요합니다.

                    스티커판 이름, 스티커 개수, 디자인, 미션 목록, 최종 보상을 body로 입력해야 합니다.
                    
                    boardDesign: TIGER, PANDA, CAT
                    
                    stickerCount: TWENTY, THIRTY, FIFTY
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커판 생성 성공"),
            @ApiResponse(responseCode = "400", description = "이미 활성화 되어있는 스티커판이 존재"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @PostMapping("/{relationId}")
    public ResponseEntity<StickerBoardCreateResponse> createStickerBoard(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId,
            @Valid @RequestBody StickerBoardCreateRequest request
    ) {
        Long stickerBoardId = stickerBoardService.createStickerBoard(userId, relationId, request);
        return ResponseEntity.ok(new StickerBoardCreateResponse(stickerBoardId));
    }

    @Operation(
            summary = "스티커판 조회",
            description =
                    """
                    토닥이 유저가 관계 id로 스티커판을 조회합니다.

                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 관계 id가 필요합니다.

                    스티커판 이름, 잔여 스티커 개수, 디자인, 최종 보상, 미션 목록을 반환합니다.
                    
                    boardDesign: TIGER, PANDA, CAT
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커판 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저, 관계, 또는 스티커판이 존재하지 않음")
    })
    @GetMapping("/{relationId}")
    public ResponseEntity<StickerBoardTodakGetResponse> getStickerBoard(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId
    ) {
        return ResponseEntity.ok(stickerBoardService.getStickerBoardByRelationId(userId, relationId));
    }

    @Operation(
            summary = "스티커판 수정",
            description =
                    """
                    토닥이 유저가 스티커판을 수정합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 스티커판 id가 필요합니다.
                    
                    스티커판 이름, 스티커 개수, 디자인, 최종 보상을 수정할 수 있습니다.
                    
                    수정에 성공하면 스티커판을 다시 조회한 결과를 반환합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커판 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 값"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 스티커판이 존재하지 않음")
    })
    @PatchMapping("/{stickerBoardId}")
    public ResponseEntity<StickerBoardTodakGetResponse> updateStickerBoard(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long stickerBoardId,
            @RequestBody @Valid StickerBoardUpdateRequest request
    ) {
        return ResponseEntity.ok(stickerBoardService.updateStickerBoard(userId, stickerBoardId, request));
    }

    @Operation(
            summary = "추억 저장소 조회",
            description =
                    """
                    토닥이 유저가 완성된 스티커판 목록을 조회합니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 관계 id가 필요합니다.
                    
                    완성된 스티커판의 타이틀, 스티커 세부 내용, 보상 내용을 반환합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 관계의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추억 저장소 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @GetMapping("/{relationId}/memory")
    public ResponseEntity<StickerBoardListMemoryResponse> getCompletedStickerBoards(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId
    ) {
        return ResponseEntity.ok(stickerBoardService.getCompletedStickerBoardsByRelation(userId, relationId));
    }

    @Operation(
            summary = "스티커판 강제 완료",
            description =
                    """
                    토닥이 유저가 스티커판을 강제로 완료시킵니다.
                    
                    헤더에 토닥이 유저의 accessToken을 담아 호출해야 하며, 경로 변수에 스티커판 id가 필요합니다.
                    
                    해당 스티커판이 채워지지 않았더라도, 강제로 완료가 가능합니다.
                    
                    성장이 유저가 이 API를 실행할 경우, 에러가 발생합니다.
                    
                    해당 스티커판의 토닥이가 아닐 경우에도 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "추억 저장소 조회 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @PostMapping("/{stickerBoardId}/complete")
    public ResponseEntity<Void> forceCompleteStickerBoard(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long stickerBoardId
    ) {
        stickerBoardService.forceComplete(userId, stickerBoardId);
        return ResponseEntity.noContent().build();
    }
}
