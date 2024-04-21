package com.tripsnap.api.auth;

import com.tripsnap.api.auth.redis.RefreshToken;
import com.tripsnap.api.auth.redis.RefreshTokenRepository;
import com.tripsnap.api.auth.vo.DecryptedToken;
import com.tripsnap.api.utils.TimeUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.AeadAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TokenService {
    public static final String TOKEN_TYPE = "Bearer ";

    private final AeadAlgorithm enc = Jwts.ENC.A256GCM; //or A128GCM, A192GCM, A256CBC-HS512, etc...
    private final SecretKey key = enc.key().build();

    private final RefreshTokenRepository refreshTokenRepository;

    private final int ACCESS_TOKEN_TIME = 60 * 10;
    private final int REFRESH_TOKEN_TIME = 60 * 60 * 24;

    public void setAccessTokenToResponse(String token, HttpServletResponse response) {
        response.setHeader(HttpHeaders.AUTHORIZATION,token);
    }
    public void setRefreshTokenToResponse(String token, HttpServletResponse response) {
        response.setHeader("Refresh-Token",token);
    }

    public String createAccessToken(String email, String role) {
        Date issuedAt = new Date();
        return Jwts.builder()
                .header().and()
                .issuer(email)
                .issuedAt(issuedAt)
                .expiration(TimeUtil.timeCalc(issuedAt, ACCESS_TOKEN_TIME))
                .claims(Map.of("role", role))
                .encryptWith(key, enc)
                .compact();
    }

    public DecryptedToken verifyAccessToken(String jwe) throws BadCredentialsException {
        try {
            Jwe<Claims> claimsJwe = Jwts.parser().decryptWith(key).build().parseEncryptedClaims(jwe);
            Claims payload = claimsJwe.getPayload();
            String email = payload.getIssuer();
            String role = String.valueOf(payload.get("role"));

            return new DecryptedToken(email, role);
        } catch (ExpiredJwtException e) {

            String email = e.getClaims().getIssuer();
            String role = e.getClaims().get("role") == null ? null : String.valueOf(e.getClaims().get("role"));

            return new DecryptedToken(email, role, true);
        } catch (JwtException | IllegalArgumentException e ) {
            throw new BadCredentialsException("bad jwt token.", e);
        }
    }

    public String expireAccessToken(String email) {
        Date issuedAt = TimeUtil.timeCalc(new Date(), (-1) * ACCESS_TOKEN_TIME);
        return Jwts.builder()
                .header().and()
                .issuer(email)
                .issuedAt(issuedAt)
                .expiration(issuedAt)
                .encryptWith(key, enc)
                .compact();
    }

    public String createRefreshToken(String email) {
        LocalDateTime issuedAt = LocalDateTime.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .email(email).uuid(String.valueOf(UUID.randomUUID()))
                .expiration(issuedAt.plusSeconds(REFRESH_TOKEN_TIME))
                .build();

        RefreshToken token = refreshTokenRepository.save(refreshToken);
        return token.getUuid();
    }

    public boolean verifyRefreshToken(String token, String email) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(email);
        if(refreshToken.isPresent()) {
            String uuid = refreshToken.get().getUuid();
            LocalDateTime expiration = refreshToken.get().getExpiration();
            if(StringUtils.hasText(uuid) && uuid.equals(token) && LocalDateTime.now().isBefore(expiration)) {
                return true;
            }
            removeRefreshToken(email);
        }
        throw new BadCredentialsException("bad refresh token");
    }

    public void removeRefreshToken(String email) {
        refreshTokenRepository.deleteById(email);
    }

}
