package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record MissionCreateRequest(
        @NotBlank String name,
        @NotBlank String emoticon,
        @Min(1) int rewardStickerCount,
        @Min(1) int targetCount
) {
}
