package kr.omong.todagtodag.domain.user.repository;

import java.util.Optional;
import kr.omong.todagtodag.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderId(String providerId);
}
