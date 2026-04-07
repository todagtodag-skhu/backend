package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StickerBoardRepository extends JpaRepository<StickerBoard, Long> {

    Optional<StickerBoard> findByUserRelationAndIsCompleted(UserRelation userRelation, boolean isCompleted);

    List<StickerBoard> findAllByUserRelationAndIsCompleted(UserRelation userRelation, boolean isCompleted);
}
