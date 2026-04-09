package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StickerAttachRequest(
        @NotNull Long pendingStickerId,
        @Min(1) int position
) {
}
