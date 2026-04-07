package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.MissionRequest;
import kr.omong.todagtodag.domain.sticker.entity.StickerBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MissionRequestRepository extends JpaRepository<MissionRequest, Long> {
    List<MissionRequest> findAllByMission_StickerBoard(StickerBoard stickerBoard);
}
