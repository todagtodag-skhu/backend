package kr.omong.todagtodag.domain.auth.repository;

import java.util.List;
import java.util.Optional;
import kr.omong.todagtodag.domain.auth.entity.RefreshToken;
import kr.omong.todagtodag.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserAndRevokedFalse(User user);

    void deleteAllByUser(User user);
}
