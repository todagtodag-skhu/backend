package kr.omong.todagtodag.domain.sticker.dto;

import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;

import java.util.List;

public record StickerBoardTodakGetResponse(
        Long stickerBoardId,
        String name,
        String remainingStickerCount,  // ex) 3/30
        BoardDesign boardDesign,
        String finalReward,
        List<MissionGetResponse> missions
) {
}
