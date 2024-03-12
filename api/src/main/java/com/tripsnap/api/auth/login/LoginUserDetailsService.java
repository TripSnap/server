package com.tripsnap.api.auth.login;

import com.tripsnap.api.auth.Roles;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    final private MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Member member = memberRepository.findByEmail(username);
            if(member != null) {
                User user = new User(member.getEmail(), member.getPassword(), List.of(new SimpleGrantedAuthority(Roles.USER)));
                return user;
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        }catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("user not found",e);
        }
    }
}
