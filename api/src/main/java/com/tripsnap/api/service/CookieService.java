package com.tripsnap.api.service;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieService {
    @Value("${service.domain.root}")
    private String rootDomain;

    public void setCookie(Cookie cookie) {
        cookie.setPath("/");
        cookie.setDomain(rootDomain);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "None");
    }

}
