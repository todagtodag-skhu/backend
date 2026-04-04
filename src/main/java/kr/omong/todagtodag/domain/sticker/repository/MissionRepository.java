package kr.omong.todagtodag.domain.sticker.repository;

import kr.omong.todagtodag.domain.sticker.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
