package com.imures.kaadbackend.language.service;

import com.imures.kaadbackend.language.controller.request.LanguageRequest;
import com.imures.kaadbackend.language.controller.response.LanguageResponse;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.language.mapper.LanguageMapper;
import com.imures.kaadbackend.language.repository.LanguageRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Transactional(readOnly = true)
    public List<LanguageResponse> getAllLanguages() {
        return languageRepository.findAll().stream()
                .map(
                    languageMapper::fromEntityToResponse
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public LanguageResponse getLanguageById(Long languageId) {
        return languageMapper.fromEntityToResponse(
                languageRepository.findById(languageId)
                        .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s, not found", languageId)))
        );
    }

    @Transactional
    public LanguageResponse createLanguage(LanguageRequest languageRequest, MultipartFile image) throws IOException {
        Language language = new Language();

        language.setLanguage(languageRequest.getLanguage());
        language.setCode(languageRequest.getCode());

        updateDefaultLanguage(languageRequest, language);
        language.setImageData(image.getBytes());

        Language savedLanguage = languageRepository.save(language);
        return languageMapper.fromEntityToResponse(savedLanguage);
    }

    @Transactional
    public LanguageResponse updateLanguage(Long languageId, LanguageRequest languageRequest, MultipartFile image) throws IOException {
        Language language = languageRepository.findById(languageId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s, not found", languageId)));

        Optional.ofNullable(languageRequest.getLanguage()).ifPresent(language::setLanguage);
        Optional.ofNullable(languageRequest.getCode()).ifPresent(language::setCode);
        Optional.ofNullable(languageRequest.getDefaultLanguage()).ifPresent((bool)-> updateDefaultLanguage(languageRequest, language));

        if(image != null) {
            language.setImageData(image.getBytes());
        }

        return languageMapper.fromEntityToResponse(languageRepository.save(language));
    }

    private void updateDefaultLanguage(LanguageRequest languageRequest, Language language) {
        if(languageRequest.getDefaultLanguage()) {
            try{
                Language defaultLanguage = languageRepository
                        .findByDefaultLanguage(true)
                        .orElseThrow(()-> new EntityNotFoundException("Default language not found"));

                defaultLanguage.setDefaultLanguage(false);
                languageRepository.save(defaultLanguage);
            }catch (EntityNotFoundException e){
                System.out.println("No default language found");
            }finally {
                language.setDefaultLanguage(true);
            }
        }else{
            language.setDefaultLanguage(false);
        }
    }

    public void deleteLanguage(Long languageId) {
        Language language = languageRepository.findById(languageId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s, not found", languageId)));
        languageRepository.delete(language);
    }

    public byte[] getLanguageImage(Long languageId) {
        Language language = languageRepository.findById(languageId)
                .orElseThrow(()-> new EntityNotFoundException(String.format("Language with id: %s, not found", languageId)));
        return language.getImageData();
    }
}
