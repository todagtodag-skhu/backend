package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.constraints.Min;

public record StickerAttachRequest(
        @Min(1) int position
) {
}
