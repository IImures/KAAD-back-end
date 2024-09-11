package com.imures.kaadbackend.specialization.repository;

import com.imures.kaadbackend.specialization.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {
}
