package com.tripsnap.api.service.aws;

import com.tripsnap.api.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.cookie.CookiesForCustomPolicy;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@Service
public class SignedCookieService {
    private final CookieService cookieService;
    @Value("${service.cloudfront.private-key}")
    private String privateKey;
    @Value("${service.domain.image}")
    private String imageUrl;
    @Value("${service.cloudfront.key-pair-id}")
    private String keyPairId;

    public void create(HttpServletResponse response) throws Exception {
        CloudFrontUtilities cloudFrontUtilities = CloudFrontUtilities.create();
        Instant expirationDate = Instant.now().plus(1, ChronoUnit.DAYS);
        String resourceUrl = "https://" + imageUrl + "/*";
        CustomSignerRequest customSignerRequest = CustomSignerRequest.builder()
                .resourceUrl(resourceUrl)
                .privateKey(new java.io.File(privateKey).toPath())
                .keyPairId(keyPairId)
                .expirationDate(expirationDate)
                .build();
        CookiesForCustomPolicy policy = cloudFrontUtilities.getCookiesForCustomPolicy(customSignerRequest);
        response.addCookie(createCookie(policy.policyHeaderValue()));
        response.addCookie(createCookie(policy.signatureHeaderValue()));
        response.addCookie(createCookie(policy.keyPairIdHeaderValue()));

    }

    private Cookie createCookie(String headerValue) {
        String[] str = headerValue.split("=");
        Cookie cookie = new Cookie(str[0], str[1]);
        cookieService.setCookie(cookie);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
