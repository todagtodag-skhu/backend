package kr.omong.todagtodag.domain.sticker.dto;

import java.util.List;

public record MissionGetResponse(
        Long missionId,
        String name,
        List<Day> days,
        Integer dailyCount,
        int rewardStickerCount
) {
}
