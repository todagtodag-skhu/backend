package kr.omong.todagtodag.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import javax.crypto.spec.SecretKeySpec;
import kr.omong.todagtodag.auth.config.JwtProperties;
import kr.omong.todagtodag.auth.exception.AuthErrorCode;
import kr.omong.todagtodag.auth.exception.AuthException;
import kr.omong.todagtodag.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(String.valueOf(user.getId()))
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(jwtProperties.accessTokenExpirationMillSecond())))
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .build();

        SignedJWT signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.HS256)
                        .type(JOSEObjectType.JWT)
                        .build(),
                claimsSet
        );

        try {
            signedJWT.sign(new MACSigner(secretKey()));
            return signedJWT.serialize();
        } catch (JOSEException exception) {
            throw new AuthException(AuthErrorCode.ACCESS_TOKEN_INVALID, exception);
        }
    }

    public JwtAuthentication getAuthentication(String token) {
        JWTClaimsSet claims = parseAndValidate(token);
        try {
            Long userId = claims.getLongClaim("userId");
            String role = claims.getStringClaim("role");
            return new JwtAuthentication(
                    userId,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
        } catch (ParseException exception) {
            throw new AuthException(AuthErrorCode.ACCESS_TOKEN_INVALID, exception);
        }
    }

    public boolean validateToken(String token) {
        parseAndValidate(token);
        return true;
    }

    private JWTClaimsSet parseAndValidate(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean verified = signedJWT.verify(new MACVerifier(secretKey().getEncoded()));
            if (!verified) {
                throw new AuthException(AuthErrorCode.ACCESS_TOKEN_INVALID);
            }

            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.toInstant().isBefore(Instant.now())) {
                throw new AuthException(AuthErrorCode.ACCESS_TOKEN_EXPIRED);
            }
            return claims;
        } catch (ParseException | JOSEException exception) {
            throw new AuthException(AuthErrorCode.ACCESS_TOKEN_INVALID, exception);
        }
    }

    private SecretKeySpec secretKey() {
        return new SecretKeySpec(jwtProperties.secret().getBytes(), "HmacSHA256");
    }
}
