package com.tripsnap.api.auth.login;

import com.tripsnap.api.auth.vo.TokenData;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;


public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private String email;

    public JWTAuthenticationToken(TokenData tokenData) {
        super(List.of(new SimpleGrantedAuthority(tokenData.role())));
        setDetails(User.builder().username(tokenData.email()).password(tokenData.email()).authorities(this.getAuthorities()).build());
        ((User)getDetails()).eraseCredentials();
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return super.getDetails();
    }
}
