package kr.omong.todagtodag.domain.sticker.model;

import kr.omong.todagtodag.domain.mission.dto.MissionGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.PendingStickerGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardMemoryResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardTodakGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerGetResponse;
import kr.omong.todagtodag.domain.sticker.entity.PendingSticker;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StickerBoardMapper {

    public static StickerBoardGetResponse toSungjangResponse(StickerBoard stickerBoard, List<PendingSticker> pendingStickers) {
        return new StickerBoardGetResponse(
                stickerBoard.getId(),
                stickerBoard.getName(),
                stickerBoard.getStickerCount(),
                stickerBoard.getBoardDesign(),
                stickerBoard.getFinalReward(),
                toStickerResponses(stickerBoard),
                toMissionResponses(stickerBoard),
                toPendingStickerResponses(pendingStickers)
        );
    }

    public static StickerBoardTodakGetResponse toTodakResponse(StickerBoard stickerBoard, Long pendingStickerCount) {
        long count = stickerBoard.getStickers().size() + pendingStickerCount;
        String currentStickerCount = count + "/" + stickerBoard.getStickerCount().getValue();

        return new StickerBoardTodakGetResponse(
                stickerBoard.getId(),
                stickerBoard.getName(),
                currentStickerCount,
                stickerBoard.getBoardDesign(),
                stickerBoard.getFinalReward(),
                toMissionResponses(stickerBoard)
        );
    }

    public static StickerBoardMemoryResponse toMemoryResponse(StickerBoard stickerBoard) {
        return new StickerBoardMemoryResponse(
                stickerBoard.getName(),
                toStickerResponses(stickerBoard),
                stickerBoard.getFinalReward()
        );
    }

    private static List<StickerGetResponse> toStickerResponses(StickerBoard stickerBoard) {
        return stickerBoard.getStickers().stream()
                .map(s -> new StickerGetResponse(
                        s.getId(),
                        s.getPosition(),
                        s.getDate(),
                        s.getContent()
                ))
                .toList();
    }

    private static List<MissionGetResponse> toMissionResponses(StickerBoard stickerBoard) {
        return stickerBoard.getMissions().stream()
                .filter(m -> !m.isCompleted())
                .map(m -> new MissionGetResponse(
                        m.getId(),
                        m.getName(),
                        m.getEmoticon(),
                        m.getRewardStickerCount(),
                        m.getTargetCount(),
                        m.getMissionRequest() != null
                ))
                .toList();
    }

    private static List<PendingStickerGetResponse> toPendingStickerResponses(List<PendingSticker> pendingStickers) {
        return pendingStickers.stream()
                .map(p -> new PendingStickerGetResponse(
                        p.getId(),
                        p.getMissionName(),
                        p.getEmoticon(),
                        p.getDate()
                ))
                .toList();
    }
}
