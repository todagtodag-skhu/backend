package kr.omong.todagtodag.domain.relation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisInviteCodeRepository implements InviteCodeRepository {

    private static final String KEY_PREFIX = "invite:";
    private static final Duration TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(String code, Long sungjangId) {
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + code, sungjangId.toString(), TTL);
    }

    @Override
    public Optional<Long> findSungjangIdByCode(String code) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + code);
        return Optional.ofNullable(value).map(Long::parseLong);
    }

    @Override
    public void delete(String code) {
        redisTemplate.delete(KEY_PREFIX + code);
    }
}
