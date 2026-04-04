package kr.omong.todagtodag.domain.sticker.dto;

import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;

import java.util.List;

public record StickerBoardCreateWithRelationRequest(
        Long relationId,
        String name,
        StickerCount stickerCount,
        BoardDesign boardDesign,
        List<MissionCreateRequest> missions,
        String finalReward
) {
    public static StickerBoardCreateWithRelationRequest of(Long relationId, StickerBoardCreateRequest request) {
        return new StickerBoardCreateWithRelationRequest(
                relationId,
                request.name(),
                request.stickerCount(),
                request.boardDesign(),
                request.missions(),
                request.finalReward()
        );
    }
}
