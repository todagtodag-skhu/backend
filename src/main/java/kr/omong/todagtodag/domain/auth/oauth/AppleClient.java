package kr.omong.todagtodag.domain.auth.oauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import kr.omong.todagtodag.domain.auth.config.OAuthProperties;
import kr.omong.todagtodag.domain.auth.exception.OAuthVerificationException;
import kr.omong.todagtodag.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class AppleClient implements OAuthTokenVerifier {

    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    private final RestClient restClient;
    private final OAuthProperties oAuthProperties;

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.APPLE;
    }

    @Override
    public VerifiedOAuthUser verify(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWSHeader header = signedJWT.getHeader();
            String keyId = header.getKeyID();

            JWKSet jwkSet = JWKSet.parse(fetchJwkSet());
            JWK jwk = jwkSet.getKeyByKeyId(keyId);
            if (!(jwk instanceof RSAKey rsaKey)) {
                throw new OAuthVerificationException(ErrorCode.APPLE_JWK_NOT_FOUND);
            }

            verifySignature(signedJWT, rsaKey);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            validateClaims(claims);

            return new VerifiedOAuthUser(
                    claims.getSubject(),
                    claims.getStringClaim("email"),
                    null
            );
        } catch (OAuthVerificationException exception) {
            throw exception;
        } catch (ParseException | RestClientException exception) {
            throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_INVALID, exception);
        }
    }

    private String fetchJwkSet() {
        try {
            String body = restClient.get()
                    .uri(oAuthProperties.keyUrl())
                    .retrieve()
                    .body(String.class);

            if (body == null || body.isBlank()) {
                throw new OAuthVerificationException(ErrorCode.APPLE_JWK_FETCH_FAILED);
            }
            return body;
        } catch (RestClientException exception) {
            throw new OAuthVerificationException(ErrorCode.APPLE_JWK_FETCH_FAILED, exception);
        }
    }

    private void verifySignature(SignedJWT signedJWT, RSAKey rsaKey) {
        try {
            JWSVerifier verifier = new RSASSAVerifier(rsaKey.toRSAPublicKey());
            boolean verified = signedJWT.verify(verifier);
            if (!verified) {
                throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_INVALID);
            }
        } catch (JOSEException exception) {
            throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_INVALID, exception);
        }
    }

    private void validateClaims(JWTClaimsSet claims) {
        if (!APPLE_ISSUER.equals(claims.getIssuer())) {
            throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_ISSUER_INVALID);
        }
        if (!claims.getAudience().contains(oAuthProperties.clientId())) {
            throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_AUDIENCE_INVALID);
        }

        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.toInstant().isBefore(Instant.now())) {
            throw new OAuthVerificationException(ErrorCode.APPLE_ID_TOKEN_EXPIRED);
        }
    }
}
