package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.omong.todagtodag.domain.sticker.dto.StickerAttachRequest;
import kr.omong.todagtodag.domain.sticker.dto.StickerAttachResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerGiveRequest;
import kr.omong.todagtodag.domain.sticker.service.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/todak/sticker")
@RequiredArgsConstructor
@Tag(name = "토닥이 스티커 API", description = "토닥이 스티커 부여")
public class TodakStickerController {

    private final StickerService stickerService;

    @Operation(
            summary = "스티커 부여",
            description =
                    """
                    토닥이가 스티커를 수동으로 부여합니다.
                    
                    헤더에 성장이 유저의 accessToken을 담아 호출해야 하며, body에 스티커의 내용을 담아 요청합니다.
                    
                    body에 content를 null로 요청했을 경우, "잘했어요" 라는 문구가 자동으로 삽입됩니다.
                    
                    이모티콘이 null일 경우에는 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커 부여 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 토닥이 유저로 요청하지 않음, 또는 해당 관계의 토닥이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저 또는 관계가 존재하지 않음")
    })
    @PostMapping("/give/{relationId}")
    public ResponseEntity<Void> attachSticker(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long relationId,
            @Valid @RequestBody StickerGiveRequest request
    ) {
        stickerService.giveSticker(userId, relationId, request);
        return ResponseEntity.ok().build();
    }
}
