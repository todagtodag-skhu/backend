package kr.omong.todagtodag.domain.mission.dto;

public record MissionRequestGetResponse(
        Long missionRequestId,
        Long missionId,
        String missionName,
        String emoticon
) {
}
