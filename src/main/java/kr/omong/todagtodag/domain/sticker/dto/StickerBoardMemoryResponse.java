package kr.omong.todagtodag.domain.sticker.dto;

import java.util.List;

public record StickerBoardMemoryResponse(
        String name,
        List<StickerGetResponse> stickers,
        String finalReward
) {
}
