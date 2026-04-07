package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.Mission;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByStickerBoardAndIsCompleted(StickerBoard stickerBoard, boolean isCompleted);
}
