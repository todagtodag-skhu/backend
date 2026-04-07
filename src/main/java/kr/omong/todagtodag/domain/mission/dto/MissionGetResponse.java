package kr.omong.todagtodag.domain.mission.dto;

public record MissionGetResponse(
        Long missionId,
        String name,
        String emoticon,
        int rewardStickerCount,
        int targetCount,
        boolean isRequested
) {
}
