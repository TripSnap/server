package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>,CustomMemberRepository {
    Optional<Member> findByEmail(String email);
}
