package com.imures.kaadbackend.specialization.repository;

import com.imures.kaadbackend.specialization.entity.SpecializationPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationPageRepository extends JpaRepository<SpecializationPage, Long> {
}
