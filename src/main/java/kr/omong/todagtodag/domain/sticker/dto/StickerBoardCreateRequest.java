package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.omong.todagtodag.domain.sticker.entity.BoardDesign;
import kr.omong.todagtodag.domain.sticker.entity.StickerCount;

import java.util.List;

public record StickerBoardCreateRequest(
        @NotBlank String name,
        @NotNull StickerCount stickerCount,
        @NotNull BoardDesign boardDesign,
        @NotEmpty @Valid List<MissionCreateRequest> missions,
        @NotBlank String finalReward
) {

}
