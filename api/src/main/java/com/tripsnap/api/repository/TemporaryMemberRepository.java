package com.tripsnap.api.repository;

import com.tripsnap.api.domain.entity.TemporaryMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemporaryMemberRepository extends JpaRepository<TemporaryMember, Long> {
    Optional<TemporaryMember> findByEmail(String email);
}
