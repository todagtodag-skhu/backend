package kr.omong.todagtodag.domain.sticker.dto;

import java.util.List;

public record StickerBoardListMemoryResponse(
        List<StickerBoardMemoryResponse> stickerBoards
) {
}
