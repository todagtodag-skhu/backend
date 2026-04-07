package kr.omong.todagtodag.domain.sticker.service;

import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.sticker.dto.MissionGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardListMemoryResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardMemoryResponse;
import kr.omong.todagtodag.domain.sticker.exception.StickerBoardException;
import kr.omong.todagtodag.domain.sticker.dto.MissionCreateRequest;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardCreateRequest;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardTodakGetResponse;
import kr.omong.todagtodag.domain.sticker.dto.StickerGetResponse;
import kr.omong.todagtodag.domain.sticker.entity.Mission;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import kr.omong.todagtodag.domain.sticker.repository.MissionRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerBoardRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StickerBoardService {

    private final StickerBoardRepository stickerBoardRepository;
    private final MissionRepository missionRepository;
    private final UserRelationRepository userRelationRepository;
    private final UserRepository userRepository;
    private final RelationService relationService;

    @Transactional
    public Long createStickerBoard(Long todakId, Long relationId, StickerBoardCreateRequest request) {
        User todak = findUserById(todakId);
        validateTodakRole(todak);

        UserRelation relation = findRelationById(relationId);
        relationService.validateRelation(todak, relation);

        StickerBoard stickerBoard = saveStickerBoard(relation, request);
        saveMissions(stickerBoard, request.missions());

        return stickerBoard.getId();
    }

    @Transactional(readOnly = true)
    public StickerBoardGetResponse getStickerBoard(Long sungjangId, Long relationId) {
        User sungjang = findUserById(sungjangId);
        validateSungjangRole(sungjang);

        UserRelation relation = findRelationById(relationId);
        validateSungjangInRelation(sungjang, relation);

        StickerBoard stickerBoard = findActiveStickerBoard(relation);
        return toResponse(stickerBoard);
    }

    @Transactional(readOnly = true)
    public StickerBoardTodakGetResponse getStickerBoardByRelationId(Long todakId, Long relationId) {
        User todak = findUserById(todakId);
        validateTodakRole(todak);

        UserRelation relation = findRelationById(relationId);
        relationService.validateRelation(todak, relation);

        StickerBoard stickerBoard = findStickerBoardByRelation(relation);
        return toTodakResponse(stickerBoard);
    }

    @Transactional(readOnly = true)
    public StickerBoardListMemoryResponse getCompletedStickerBoards(Long sungjangId) {
        User sungjang = findUserById(sungjangId);
        validateSungjangRole(sungjang);

        UserRelation relation = findRelationBySungjang(sungjang);

        List<StickerBoardMemoryResponse> stickerBoards =
                stickerBoardRepository.findAllByUserRelationAndIsCompleted(relation, true)
                        .stream()
                        .map(this::toMemoryResponse)
                        .toList();

        return new StickerBoardListMemoryResponse(stickerBoards);
    }

    @Transactional(readOnly = true)
    public StickerBoardListMemoryResponse getCompletedStickerBoardsByRelation(Long todakId, Long relationId) {
        User todak = findUserById(todakId);
        validateTodakRole(todak);

        UserRelation relation = findRelationById(relationId);
        relationService.validateRelation(todak, relation);

        List<StickerBoardMemoryResponse> stickerBoards =
                stickerBoardRepository.findAllByUserRelationAndIsCompleted(relation, true)
                        .stream()
                        .map(this::toMemoryResponse)
                        .toList();

        return new StickerBoardListMemoryResponse(stickerBoards);
    }

    private StickerBoard saveStickerBoard(UserRelation relation, StickerBoardCreateRequest request) {
        return stickerBoardRepository.save(
                StickerBoard.builder()
                        .userRelation(relation)
                        .name(request.name())
                        .stickerCount(request.stickerCount())
                        .boardDesign(request.boardDesign())
                        .finalReward(request.finalReward())
                        .build()
        );
    }

    private void saveMissions(StickerBoard stickerBoard, List<MissionCreateRequest> missions) {
        List<Mission> missionEntities = missions.stream()
                .map(m -> Mission.builder()
                        .stickerBoard(stickerBoard)
                        .name(m.name())
                        .days(m.days())
                        .dailyCount(m.dailyCount())
                        .rewardStickerCount(m.rewardStickerCount())
                        .build())
                .toList();
        missionRepository.saveAll(missionEntities);
    }

    private void validateTodakRole(User user) {
        if (!user.getRole().equals(Role.TODAK)) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private void validateSungjangRole(User user) {
        if (!user.getRole().equals(Role.SUNGJANG)) {
            throw new RelationException(ErrorCode.ROLE_MISMATCH);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }

    private UserRelation findRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }

    private void validateSungjangInRelation(User sungjang, UserRelation relation) {
        if (!relation.getSungjang().getId().equals(sungjang.getId())) {
            throw new RelationException(ErrorCode.RELATION_SUNGJANG_MISMATCH);
        }
    }

    private StickerBoardGetResponse toResponse(StickerBoard stickerBoard) {
        List<StickerGetResponse> stickers = stickerBoard.getStickers().stream()
                .map(s -> new StickerGetResponse(
                        s.getId(),
                        s.getPosition(),
                        s.getDate(),
                        s.getContent()
                ))
                .toList();

        return new StickerBoardGetResponse(
                stickerBoard.getId(),
                stickerBoard.getName(),
                stickerBoard.getStickerCount(),
                stickerBoard.getBoardDesign(),
                stickerBoard.getFinalReward(),
                stickers
        );
    }

    private StickerBoard findActiveStickerBoard(UserRelation relation) {
        return stickerBoardRepository.findByUserRelationAndIsCompleted(relation, false)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }

    private StickerBoard findStickerBoardByRelation(UserRelation relation) {
        return stickerBoardRepository.findByUserRelationAndIsCompleted(relation, false)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.STICKER_BOARD_NOT_FOUND));
    }

    private StickerBoardTodakGetResponse toTodakResponse(StickerBoard stickerBoard) {
        String remainingStickerCount = stickerBoard.getStickers().size()
                + "/" + stickerBoard.getStickerCount().getValue();

        List<MissionGetResponse> missions = stickerBoard.getMissions().stream()
                .map(m -> new MissionGetResponse(
                        m.getId(),
                        m.getName(),
                        List.copyOf(m.getDays()),
                        m.getDailyCount(),
                        m.getRewardStickerCount()
                ))
                .toList();

        return new StickerBoardTodakGetResponse(
                stickerBoard.getId(),
                stickerBoard.getName(),
                remainingStickerCount,
                stickerBoard.getBoardDesign(),
                stickerBoard.getFinalReward(),
                missions
        );
    }

    private StickerBoardMemoryResponse toMemoryResponse(StickerBoard stickerBoard) {
        List<StickerGetResponse> stickers = stickerBoard.getStickers().stream()
                .map(s -> new StickerGetResponse(
                        s.getId(),
                        s.getPosition(),
                        s.getDate(),
                        s.getContent()
                ))
                .toList();

        return new StickerBoardMemoryResponse(
                stickerBoard.getName(),
                stickers,
                stickerBoard.getFinalReward()
        );
    }

    private UserRelation findRelationBySungjang(User sungjang) {
        return userRelationRepository.findBySungjang(sungjang)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }
}
