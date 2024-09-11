package com.imures.kaadbackend.teammembder.repository;

import com.imures.kaadbackend.teammembder.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}
