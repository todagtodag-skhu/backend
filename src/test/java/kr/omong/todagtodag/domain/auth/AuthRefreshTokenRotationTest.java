package kr.omong.todagtodag.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import kr.omong.todagtodag.domain.auth.entity.RefreshToken;
import kr.omong.todagtodag.domain.auth.repository.RefreshTokenRepository;
import kr.omong.todagtodag.domain.auth.scheduler.RefreshTokenSchedular;
import kr.omong.todagtodag.domain.user.entity.Role;
import kr.omong.todagtodag.domain.user.entity.User;
import kr.omong.todagtodag.domain.user.repository.UserRepository;
import kr.omong.todagtodag.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {
        "app.jwt.secret=01234567890123456789012345678901",
        "app.jwt.access-token-expiration-mill-second=86400000",
        "app.jwt.refresh-token-expiration-mill-second=1209600000",
        "app.oauth.apple.client-id=test-client-id"
})
@AutoConfigureMockMvc
@ActiveProfiles("local")
class AuthRefreshTokenRotationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenSchedular refreshTokenSchedular;

    @Test
    void pendingUserCanRefreshButPreviousRefreshTokenIsRevokedAndCannotBeReused() throws Exception {
        JsonNode pendingUser = createTestUser("rtr-pending-" + UUID.randomUUID());
        String firstAccessToken = pendingUser.get("accessToken").asText();
        String firstRefreshToken = pendingUser.get("refreshToken").asText();

        JsonNode refreshed = refresh(firstRefreshToken);
        String secondRefreshToken = refreshed.get("refreshToken").asText();

        assertThat(refreshed.get("role").asText()).isEqualTo(Role.PENDING.name());
        assertThat(secondRefreshToken).isNotEqualTo(firstRefreshToken);
        assertThat(refreshTokenRepository.findByToken(firstRefreshToken)).hasValueSatisfying(
                refreshToken -> assertThat(refreshToken.isRevoked()).isTrue()
        );
        assertThat(refreshTokenRepository.findByToken(secondRefreshToken)).hasValueSatisfying(
                refreshToken -> assertThat(refreshToken.isRevoked()).isFalse()
        );

        refreshExpectError(firstRefreshToken, ErrorCode.REFRESH_TOKEN_REVOKED);
        logoutExpectError(firstAccessToken, firstRefreshToken, ErrorCode.REFRESH_TOKEN_REVOKED);

        logout(refreshed.get("accessToken").asText(), secondRefreshToken);
        assertThat(refreshTokenRepository.findByToken(secondRefreshToken)).isEmpty();
    }

    @Test
    void expiredRefreshTokenIsRejectedAndDeleted() throws Exception {
        User user = userRepository.save(User.builder()
                .providerId("rtr-expired-" + UUID.randomUUID())
                .role(Role.PENDING)
                .build());
        String expiredToken = "expired-refresh-token-" + UUID.randomUUID();
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(expiredToken)
                .expiryDate(LocalDateTime.now().minusSeconds(1))
                .createdAt(LocalDateTime.now().minusDays(1))
                .build());

        refreshExpectError(expiredToken, ErrorCode.REFRESH_TOKEN_EXPIRED);

        assertThat(refreshTokenRepository.findByToken(expiredToken)).isEmpty();
    }

    @Test
    void schedulerDeletesOnlyRevokedRefreshTokens() {
        User user = userRepository.save(User.builder()
                .providerId("rtr-scheduler-" + UUID.randomUUID())
                .role(Role.PENDING)
                .build());
        LocalDateTime now = LocalDateTime.now();
        String revokedToken = "revoked-refresh-token-" + UUID.randomUUID();
        String activeToken = "active-refresh-token-" + UUID.randomUUID();
        RefreshToken revoked = RefreshToken.builder()
                .user(user)
                .token(revokedToken)
                .expiryDate(now.plusDays(1))
                .createdAt(now)
                .build();
        revoked.revoke();
        refreshTokenRepository.save(revoked);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(activeToken)
                .expiryDate(now.plusDays(1))
                .createdAt(now)
                .build());

        refreshTokenSchedular.deleteRevokedTokens();

        assertThat(refreshTokenRepository.findByToken(revokedToken)).isEmpty();
        assertThat(refreshTokenRepository.findByToken(activeToken)).isPresent();
    }

    private JsonNode createTestUser(String providerId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/test/test-user")
                        .param("providerId", providerId))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode refresh(String refreshToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest(refreshToken)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private void refreshExpectError(String refreshToken, ErrorCode errorCode) throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest(refreshToken)))
                .andExpect(status().is(errorCode.getStatus().value()))
                .andExpect(jsonPath("$.code").value(errorCode.name()));
    }

    private void logout(String accessToken, String refreshToken) throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest(refreshToken)))
                .andExpect(status().isOk());
    }

    private void logoutExpectError(String accessToken, String refreshToken, ErrorCode errorCode) throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, bearer(accessToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshRequest(refreshToken)))
                .andExpect(status().is(errorCode.getStatus().value()))
                .andExpect(jsonPath("$.code").value(errorCode.name()));
    }

    private String refreshRequest(String refreshToken) {
        return """
                {
                  "refreshToken": "%s"
                }
                """.formatted(refreshToken);
    }

    private String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }
}
