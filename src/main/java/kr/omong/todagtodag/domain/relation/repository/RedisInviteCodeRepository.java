package kr.omong.todagtodag.domain.relation.repository;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
@RequiredArgsConstructor
public class RedisInviteCodeRepository implements InviteCodeRepository {

    private static final Duration TTL = Duration.ofMinutes(5);
    private static final String PREFIX = "invite-code:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String code, Long sungjangId) {
        redisTemplate.opsForValue().set(PREFIX + code, String.valueOf(sungjangId), TTL);
    }

    @Override
    public Optional<Long> findSungjangIdByCode(String code) {
        String value = redisTemplate.opsForValue().get(PREFIX + code);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(Long.valueOf(value));
    }

    @Override
    public void delete(String code) {
        redisTemplate.delete(PREFIX + code);
    }
}
