package com.tripsnap.api.auth.login;

import com.tripsnap.api.auth.Roles;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LoginUserDetailsService implements UserDetailsService {

    final private MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Optional<Member> optMember = memberRepository.findByEmail(username);
            if(optMember.isPresent()) {
                Member member = optMember.get();
                UserDetails user = User.withUsername(member.getEmail()).password(member.getPassword()).roles(Roles.USER).build();
                return user;
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        }catch (EntityNotFoundException e) {
            throw new UsernameNotFoundException("user not found",e);
        }
    }
}
