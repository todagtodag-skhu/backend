package kr.omong.todagtodag.domain.sticker.service;

import kr.omong.todagtodag.domain.auth.exception.AuthException;
import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.exception.RelationException;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.sticker.dto.MissionCreateRequest;
import kr.omong.todagtodag.domain.sticker.dto.StickerBoardCreateWithRelationRequest;
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
    public Long createStickerBoard(Long todakId, StickerBoardCreateWithRelationRequest request) {
        User todak = findUserById(todakId);
        validateTodakRole(todak);

        UserRelation relation = findRelationById(request.relationId());
        relationService.validateRelation(todak, relation);

        StickerBoard stickerBoard = saveStickerBoard(relation, request);
        saveMissions(stickerBoard, request.missions());

        return stickerBoard.getId();
    }

    private StickerBoard saveStickerBoard(UserRelation relation, StickerBoardCreateWithRelationRequest request) {
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
    }

    private UserRelation findRelationById(Long relationId) {
        return userRelationRepository.findById(relationId)
                .orElseThrow(() -> new RelationException(ErrorCode.RELATION_NOT_FOUND));
    }
}
