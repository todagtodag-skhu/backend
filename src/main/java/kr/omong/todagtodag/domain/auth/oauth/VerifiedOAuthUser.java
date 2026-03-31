package kr.omong.todagtodag.domain.auth.oauth;

public record VerifiedOAuthUser(
        String providerId,
        String email,
        String name
) {
}
