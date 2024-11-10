package com.imures.kaadbackend.specialization.repository;

import com.imures.kaadbackend.specialization.entity.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecializationRepository extends JpaRepository<Specialization, Long> {

    @Query("SELECT s FROM specialization s WHERE :includeHidden = true OR s.isHidden = false")
    List<Specialization> findSpecializations(boolean includeHidden);

    default List<Specialization> findSpecializations() {
        return findSpecializations(false);
    }
}
