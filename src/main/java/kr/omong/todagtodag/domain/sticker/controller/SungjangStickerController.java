package kr.omong.todagtodag.domain.sticker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.omong.todagtodag.domain.sticker.dto.StickerAttachRequest;
import kr.omong.todagtodag.domain.sticker.service.StickerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sungjang/sticker")
@RequiredArgsConstructor
@Tag(name = "성장이 스티커 API", description = "성장이 스티커 부착")
public class SungjangStickerController {

    private final StickerService stickerService;

    @Operation(
            summary = "스티커 부착",
            description =
                    """
                    성장이가 스티커를 원하는 위치에 부착합니다.
                    
                    헤더에 성장이 유저의 accessToken을 담아 호출해야 하며, body에 해당 스티커의 위치를 담아 요청합니다.
                    
                    해당 자리에 이미 스티커가 존재할 경우 에러가 발생합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스티커 부착 성공"),
            @ApiResponse(responseCode = "403", description = "토큰이 없거나, 성장이 유저로 요청하지 않음, 또는 해당 관계의 성장이가 아님"),
            @ApiResponse(responseCode = "404", description = "유저, 관계 또는 스티커판이 존재하지 않음"),
            @ApiResponse(responseCode = "409", description = "해당 위치에 이미 스티커가 존재함")
    })
    @PostMapping("/attach")
    public ResponseEntity<Void> attachSticker(
            @AuthenticationPrincipal Long userId,
            @RequestBody StickerAttachRequest request
    ) {
        stickerService.attachSticker(userId, request.position());
        return ResponseEntity.ok().build();
    }
}
