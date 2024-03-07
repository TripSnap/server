package com.tripsnap.api.auth;


import com.tripsnap.api.utils.TimeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwe;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.AeadAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JWTUtil {
    public static final String TOKEN_TYPE = "Bearer ";

    private static final AeadAlgorithm enc = Jwts.ENC.A256GCM; //or A128GCM, A192GCM, A256CBC-HS512, etc...
    private static final SecretKey key = enc.key().build();


    static public String createToken(TokenData param) {

        Date issuedAt = new Date();
        return Jwts.builder()
                .header().and()
                .issuer(param.email())
                .issuedAt(issuedAt)
                .expiration(TimeUtil.timeCalc(issuedAt, 60 * 60 * 10))
                .claims(Map.of("role", param.role()))
                .encryptWith(key, enc)
                .compact();
    }

    static public DecryptedToken verify(String jwe) {
        try {
            Jwe<Claims> claimsJwe = Jwts.parser().decryptWith(key).build().parseEncryptedClaims(jwe);
            Claims payload = claimsJwe.getPayload();
            boolean isExpired = payload.getIssuedAt().after(new Date());
            return new DecryptedToken(true, isExpired, new TokenData(payload.getIssuer(), String.valueOf(payload.get("role"))));
        } catch (JwtException | IllegalArgumentException e ) {
            return new DecryptedToken(false);
        }
    }
}
