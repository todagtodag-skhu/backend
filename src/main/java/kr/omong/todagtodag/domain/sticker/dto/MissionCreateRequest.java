package kr.omong.todagtodag.domain.sticker.dto;

public record MissionCreateRequest(
        String name,
        String emoticon,
        int rewardStickerCount,
        int targetCount
) {
}
