package com.imures.kaadbackend.language.repository;

import com.imures.kaadbackend.language.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByDefaultLanguage(Boolean defaultLanguage);

    Optional<Language> findByCode(String code);

}
