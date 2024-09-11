package com.imures.kaadbackend.generalinfo.repository;

import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralInfoRepository extends JpaRepository<GeneralInfo, Long> {
}
