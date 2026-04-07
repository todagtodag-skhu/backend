package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.Sticker;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StickerRepository extends JpaRepository<Sticker, Long> {
    boolean existsByStickerBoardAndPosition(StickerBoard stickerBoard, int position);
}
