package kr.omong.todagtodag.domain.sticker.dto;

import kr.omong.todagtodag.domain.mission.dto.MissionGetResponse;
import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;

import java.util.List;

public record StickerBoardGetResponse(
        Long stickerBoardId,
        String name,
        StickerCount stickerCount,
        BoardDesign boardDesign,
        String finalReward,
        List<StickerGetResponse> stickers,
        List<MissionGetResponse> missions,
        List<PendingStickerGetResponse> pendingStickers
) {
}
