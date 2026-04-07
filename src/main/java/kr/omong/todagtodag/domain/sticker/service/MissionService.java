package kr.omong.todagtodag.domain.sticker.service;

import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.sticker.dto.MissionCreateRequest;
import kr.omong.todagtodag.domain.sticker.entity.Mission;
import kr.omong.todagtodag.domain.sticker.entity.PendingSticker;
import kr.omong.todagtodag.domain.sticker.entity.Sticker;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import kr.omong.todagtodag.domain.sticker.exception.StickerBoardException;
import kr.omong.todagtodag.domain.sticker.repository.MissionRepository;
import kr.omong.todagtodag.domain.sticker.repository.PendingStickerRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerBoardRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.domain.user.service.UserService;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final StickerBoardRepository stickerBoardRepository;
    private final StickerRepository stickerRepository;
    private final UserRepository userRepository;
    private final RelationService relationService;
    private final UserService userService;
    private final PendingStickerRepository pendingStickerRepository;

    @Transactional
    public Long createMission(Long todakId, Long stickerBoardId, MissionCreateRequest request) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        StickerBoard stickerBoard = findStickerBoardById(stickerBoardId);
        relationService.validateRelation(todak, stickerBoard.getUserRelation());

        Mission mission = missionRepository.save(
                Mission.builder()
                        .stickerBoard(stickerBoard)
                        .name(request.name())
                        .emoticon(request.emoticon())
                        .rewardStickerCount(request.rewardStickerCount())
                        .targetCount(request.targetCount())
                        .build()
        );

        return mission.getId();
    }

    @Transactional(readOnly = true)
    public MissionListGetResponse getMissions(Long todakId, Long relationId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        UserRelation relation = findRelationById(relationId);
        relationService.validateRelation(todak, relation);

        StickerBoard stickerBoard = findActiveStickerBoard(relation);

        List<MissionGetResponse> missions = missionRepository.findAllByStickerBoardAndIsCompleted(stickerBoard, false)
                .stream()
                .map(m -> new MissionGetResponse(
                        m.getId(),
                        m.getName(),
                        m.getEmoticon(),
                        m.getRewardStickerCount(),
                        m.getTargetCount()
                ))
                .toList();

        return new MissionListGetResponse(missions);
    }

    @Transactional
    public void updateMission(Long todakId, Long missionId, MissionCreateRequest request) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        Mission mission = findMissionById(missionId);
        relationService.validateRelation(todak, mission.getStickerBoard().getUserRelation());

        mission.update(
                request.name(),
                request.emoticon(),
                request.rewardStickerCount(),
                request.targetCount()
        );
    }

    @Transactional
    public void deleteMission(Long todakId, Long missionId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        Mission mission = findMissionById(missionId);
        relationService.validateRelation(todak, mission.getStickerBoard().getUserRelation());

        missionRepository.delete(mission);
    }

    @Transactional
    public void completeMission(Long todakId, Long missionId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        Mission mission = findMissionById(missionId);
        StickerBoard stickerBoard = mission.getStickerBoard();
        relationService.validateRelation(todak, stickerBoard.getUserRelation());

        mission.complete();
        savePendingStickers(stickerBoard, mission);
    }

    private Mission findMissionById(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.MISSION_NOT_FOUND));
    }

    private void savePendingStickers(StickerBoard stickerBoard, Mission mission) {
        List<PendingSticker> pendingStickers = new ArrayList<>();
        for (int i = 0; i < mission.getRewardStickerCount(); i++) {
            pendingStickers.add(
                    PendingSticker.builder()
                            .stickerBoard(stickerBoard)
                            .missionName(mission.getName())
                            .emoticon(mission.getEmoticon())
                            .date(LocalDate.now())
                            .build()
            );
        }
        pendingStickerRepository.saveAll(pendingStickers);
    }

    private StickerBoard findStickerBoardById(Long stickerBoardId) {
        return stickerBoardRepository.findById(stickerBoardId)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.STICKER_BOARD_NOT_FOUND));
    }
}
