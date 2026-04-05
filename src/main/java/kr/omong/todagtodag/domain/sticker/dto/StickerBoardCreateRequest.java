package kr.omong.todagtodag.domain.sticker.dto;

import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;

import java.util.List;

public record StickerBoardCreateRequest(
        String name,
        StickerCount stickerCount,
        BoardDesign boardDesign,
        List<MissionCreateRequest> missions,
        String finalReward
) {

}
