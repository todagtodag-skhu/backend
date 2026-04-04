package kr.omong.todagtodag.domain.sticker.dto;

import java.time.LocalDate;

public record StickerGetResponse(
        Long stickerId,
        int position,
        LocalDate date,
        String content,
        String missionName
) {
}
