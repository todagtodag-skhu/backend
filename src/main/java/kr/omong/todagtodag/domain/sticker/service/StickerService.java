package kr.omong.todagtodag.domain.sticker.service;

import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.relation.repository.UserRelationRepository;
import kr.omong.todagtodag.domain.relation.service.RelationService;
import kr.omong.todagtodag.domain.sticker.entity.Sticker;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import kr.omong.todagtodag.domain.sticker.exception.StickerBoardException;
import kr.omong.todagtodag.domain.sticker.repository.PendingStickerRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerBoardRepository;
import kr.omong.todagtodag.domain.sticker.repository.StickerRepository;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.service.UserService;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StickerService {

    private final UserService userService;
    private final RelationService relationService;
    private final PendingStickerRepository pendingStickerRepository;
    private final StickerBoardRepository stickerBoardRepository;
    private final UserRelationRepository userRelationRepository;
    private final StickerRepository stickerRepository;

    @Transactional
    public void attachSticker(Long sungjangId, int position) {
        User sungjang = userService.getById(sungjangId);
        relationService.validateRole(sungjang, Role.SUNGJANG);

        UserRelation relation = relationService.findRelationBySungjang(sungjang);
        StickerBoard stickerBoard = findActiveStickerBoard(relation);
        validatePositionNotOccupied(stickerBoard, position);

        pendingStickerRepository.findFirstStickerBoardOrderByIdAsc(stickerBoard)
                .ifPresent(pendingSticker -> {
                    pendingStickerRepository.delete(pendingSticker);
                    stickerRepository.save(
                            Sticker.builder()
                                    .stickerBoard(stickerBoard)
                                    .position(position)
                                    .date(pendingSticker.getDate())
                                    .content(pendingSticker.getMissionName())
                                    .emoticon(pendingSticker.getEmoticon())
                                    .build()
                    );
                });
    }

    private StickerBoard findActiveStickerBoard(UserRelation relation) {
        return stickerBoardRepository.findByUserRelationAndIsCompleted(relation, false)
                .orElseThrow(() -> new StickerBoardException(ErrorCode.STICKER_BOARD_NOT_FOUND));
    }

    private void validatePositionNotOccupied(StickerBoard stickerBoard, int position) {
        if (stickerRepository.existsByStickerBoardAndPosition(stickerBoard, position)) {
            throw new StickerBoardException(ErrorCode.STICKER_POSITION_ALREADY_OCCUPIED);
        }
    }
}
