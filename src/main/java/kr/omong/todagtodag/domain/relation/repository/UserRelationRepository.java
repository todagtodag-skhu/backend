package kr.omong.todagtodag.domain.relation.repository;

import kr.omong.todagtodag.domain.relation.entity.UserRelation;
import kr.omong.todagtodag.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {
    boolean existsByTodakIdAndSungjangId(Long toakId, Long sungjangId);

    List<UserRelation> findAllByTodakId(Long todakId);

    List<UserRelation> findAllByTodak(User todak);
}
