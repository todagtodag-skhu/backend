package kr.omong.todagtodag.domain.sticker.dto;

import java.util.List;

public record MissionCreateRequest(
        String name,
        List<Day> days,
        Integer dailyCount,
        int rewardStickerCount
) {
}
