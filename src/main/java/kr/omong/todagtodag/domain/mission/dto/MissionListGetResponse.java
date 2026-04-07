package kr.omong.todagtodag.domain.mission.dto;

import java.util.List;

public record MissionListGetResponse(
        List<MissionGetResponse> missions
) {
}
