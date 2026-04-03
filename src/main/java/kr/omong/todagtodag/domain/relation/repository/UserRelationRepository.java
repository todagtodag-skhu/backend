package kr.omong.todagtodag.domain.relation.repository;

import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {
    boolean existsByTodakIdAndSungjangId(Long toakId, Long sungjangId);

    Optional<UserRelation> findBySungjangId(Long sungjangId);

    void deleteAllByTodakIdOrSungjangId(Long todakId, Long sungjangId);
}
