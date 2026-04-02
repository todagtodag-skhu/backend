package kr.omong.todagtodag.domain.relation.repository;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class InMemoryInviteCodeRepository implements InviteCodeRepository {

    private static final Duration TTL = Duration.ofMinutes(5);

    private final Map<String, InviteCodeEntry> storage = new ConcurrentHashMap<>();

    @Override
    public void save(String code, Long sungjangId) {
        storage.put(code, new InviteCodeEntry(sungjangId, Instant.now().plus(TTL)));
    }

    @Override
    public Optional<Long> findSungjangIdByCode(String code) {
        InviteCodeEntry entry = storage.get(code);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.expiresAt().isBefore(Instant.now())) {
            storage.remove(code);
            return Optional.empty();
        }
        return Optional.of(entry.sungjangId());
    }

    @Override
    public void delete(String code) {
        storage.remove(code);
    }

    private record InviteCodeEntry(Long sungjangId, Instant expiresAt) {
    }
}
