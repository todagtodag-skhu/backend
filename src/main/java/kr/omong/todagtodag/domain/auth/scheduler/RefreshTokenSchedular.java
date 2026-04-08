package kr.omong.todagtodag.domain.auth.scheduler;

import kr.omong.todagtodag.domain.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenSchedular {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void deleteRevokedTokens() {
        refreshTokenRepository.deleteByIsRevokedTrue();
    }
}
