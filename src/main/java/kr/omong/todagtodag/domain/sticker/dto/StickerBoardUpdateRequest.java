package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;

public record StickerBoardUpdateRequest(
        @NotBlank String name,
        @NotNull StickerCount stickerCount,
        @NotNull BoardDesign boardDesign,
        @NotBlank String finalReward
) {
}
