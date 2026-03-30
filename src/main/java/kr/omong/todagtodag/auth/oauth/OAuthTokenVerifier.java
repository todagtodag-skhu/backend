package kr.omong.todagtodag.auth.oauth;

public interface OAuthTokenVerifier {

    SocialProvider getProvider();

    VerifiedOAuthUser verify(String idToken);
}
