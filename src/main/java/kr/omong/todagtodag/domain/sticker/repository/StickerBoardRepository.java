package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerBoardRepository extends JpaRepository<StickerBoard, Long> {
}
