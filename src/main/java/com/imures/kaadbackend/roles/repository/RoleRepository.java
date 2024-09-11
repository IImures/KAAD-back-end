package com.imures.kaadbackend.roles.repository;

import com.imures.kaadbackend.roles.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
