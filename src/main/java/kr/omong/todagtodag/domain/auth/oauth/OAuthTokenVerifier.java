package kr.omong.todagtodag.domain.auth.oauth;

public interface OAuthTokenVerifier {

    SocialProvider getProvider();

    VerifiedOAuthUser verify(String idToken);
}
