package kr.omong.todagtodag.auth.oauth;

public record VerifiedOAuthUser(
        String providerId,
        String email,
        String name
) {
}
