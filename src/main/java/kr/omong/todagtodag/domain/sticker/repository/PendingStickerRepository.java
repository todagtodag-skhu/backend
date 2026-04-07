package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.PendingSticker;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingStickerRepository extends JpaRepository<PendingSticker, Long> {
    Optional<PendingSticker> findFirstStickerBoardOrderByIdAsc(StickerBoard stickerBoard);
}
