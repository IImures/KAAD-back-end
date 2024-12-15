package com.imures.kaadbackend.generalinfo.repository;

import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneralInfoRepository extends JpaRepository<GeneralInfo, Long> {

    Optional<GeneralInfo> findByCodeAndLanguage_Code(String code, String languageCode);

    Optional<GeneralInfo> findByCodeAndLanguage_DefaultLanguage(String code, Boolean defaultLanguage);

    List<GeneralInfo> findGeneralInfoByIsLabelAndLanguage_DefaultLanguageTrue(Boolean isLabel);

}
