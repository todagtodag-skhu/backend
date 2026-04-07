package kr.omong.todagtodag.domain.sticker.dto;

public record MissionRequestGetResponse(
        Long missionRequestId,
        Long missionId,
        String missionName,
        String emoticon
) {
}
