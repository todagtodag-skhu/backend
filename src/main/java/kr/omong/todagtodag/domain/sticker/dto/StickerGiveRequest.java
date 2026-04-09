package kr.omong.todagtodag.domain.sticker.dto;

import jakarta.validation.constraints.NotBlank;

public record StickerGiveRequest(
        String content, // nullable
        @NotBlank String emoticon
) {
}
