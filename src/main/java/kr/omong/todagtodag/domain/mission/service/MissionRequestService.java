package kr.omong.todagtodag.domain.mission.service;

import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.mission.dto.MissionRequestGetResponse;
import kr.omong.todagtodag.domain.mission.dto.MissionRequestListGetResponse;
import kr.omong.todagtodag.domain.mission.entity.Mission;
import kr.omong.todagtodag.domain.mission.entity.MissionRequest;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import kr.omong.todagtodag.domain.sticker.exception.StickerBoardException;
import kr.omong.todagtodag.domain.mission.repository.MissionRepository;
import kr.omong.todagtodag.domain.mission.repository.MissionRequestRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerBoardRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.service.UserService;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionRequestService {

    private final UserService userService;
    private final RelationService relationService;
    private final MissionRepository missionRepository;
    private final MissionRequestRepository missionRequestRepository;
    private final UserRelationRepository userRelationRepository;
    private final StickerBoardRepository stickerBoardRepository;
    private final MissionService missionService;

    @Transactional
    public void requestMission(Long sungjangId, Long missionId) {
        User sungjang = userService.getById(sungjangId);
        relationService.validateRole(sungjang, Role.SUNGJANG);

        Mission mission = findMissionById(missionId);
        validateSungjangInRelation(sungjang, mission.getStickerBoard().getUserRelation());
        validateNotAlreadyRequested(mission);

        missionRequestRepository.save(
                MissionRequest.builder()
                        .mission(mission)
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public MissionRequestListGetResponse getMissionRequests(Long todakId, Long relationId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        UserRelation relation = findRelationById(relationId);
        relationService.validateRelation(todak, relation);

        StickerBoard stickerBoard = findActiveStickerBoard(relation);

        List<MissionRequestGetResponse> requests =
                missionRequestRepository.findAllByMission_StickerBoard(stickerBoard)
                        .stream()
                        .map(r -> new MissionRequestGetResponse(
                                r.getId(),
                                r.getMission().getId(),
                                r.getMission().getName(),
                                r.getMission().getEmoticon()
                        ))
                        .toList();
        return new MissionRequestListGetResponse(requests);
    }

    @Transactional
    public void acceptMissionRequest(Long todakId, Long missionRequestId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        MissionRequest missionRequest = findMissionRequestById(missionRequestId);
        relationService.validateRelation(todak, missionRequest.getMission().getStickerBoard().getUserRelation());

        missionService.completeMission(todak.getId(), missionRequest.getMission().getId());
        missionRequest.getMission().clearRequest();
        missionRequestRepository.delete(missionRequest);
    }

    @Transactional
    public void rejectMissionRequest(Long todakId, Long missionRequestId) {
        User todak = userService.getById(todakId);
        relationService.validateRole(todak, Role.TODAK);

        MissionRequest missionRequest = findMissionRequestById(missionRequestId);
        relationService.validateRelation(todak, missionRequest.getMission().getStickerBoard().getUserRelation());

        missionRequest.getMission().clearRequest();
        missionRequestRepository.delete(missionRequest);
    }

    private void validateNotAlreadyRequested(Mission mission) {
        if (mission.hasRequest()) {
            throw new StickerBoardException(ErrorCode.MISSION_REQUEST_ALREADY_EXISTS);
        }
    }

    private void validateSungjangInRelation(User sungjang, UserRelation relation) {
        if (!relation.getSungjang().getId().equals(sungjang.getId())) {
            throw new RelationException(ErrorCode.RELATION_TODAK_MISMATCH);
        }
    }

    private Mission findMissionById(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.MISSION_NOT_FOUND));
    }

    private MissionRequest findMissionRequestById(Long missionRequestId) {
        return missionRequestRepository.findById(missionRequestId)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.MISSION_REQUEST_NOT_FOUND));
    }

    private UserRelation findRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }

    private StickerBoard findActiveStickerBoard(UserRelation relation) {
        return stickerBoardRepository.findByUserRelationAndIsCompleted(relation, false)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.STICKER_BOARD_NOT_FOUND));
    }
}
