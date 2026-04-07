package kr.omong.todagtodag.domain.sticker.dto;

import java.util.List;

public record MissionRequestListGetResponse(
        List<MissionRequestGetResponse> requests
) {
}
