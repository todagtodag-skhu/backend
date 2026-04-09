package kr.omong.todagtodag.domain.sticker.dto;

import java.time.LocalDate;

public record PendingStickerGetResponse(
        Long pendingStickerId,
        String missionName,
        String emoticon,
        LocalDate date
) {
}
