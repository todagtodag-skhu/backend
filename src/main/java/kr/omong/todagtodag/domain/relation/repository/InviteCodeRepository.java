package kr.omong.todagtodag.domain.relation.repository;

import java.util.Optional;

public interface InviteCodeRepository {
    void save(String code, Long sungjangId);

    Optional<Long> findSungjangIdByCode(String code);

    void delete(String code);
}
