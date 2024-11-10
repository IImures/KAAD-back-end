package com.imures.kaadbackend.contact.mapper;

import com.imures.kaadbackend.contact.controller.request.ContactRequest;
import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import com.imures.kaadbackend.contact.entity.Contact;
import com.imures.kaadbackend.contact.entity.ContactType;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.specialization.entity.Specialization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    Contact fromRequestToEntity(ContactRequest contactRequest);

    @Mapping(target = "specialization", expression = "java(specialization(contact.getSpecialization()))")
    @Mapping(target = "contactType", expression = "java(contactTypeToString(contact.getContactType()))")
    @Mapping(target = "language", expression = "java(language(contact.getLanguage()))")
    ContactResponse fromEntityToResponse(Contact contact);

    default String language(Language language){
        return language.getLanguage();
    }

    default String specialization(Specialization specialization) {
        return specialization.getSpecNames()
                .stream()
                .filter(generalInfo ->
                        generalInfo.getLanguage() != null &&
                                generalInfo.getLanguage().getDefaultLanguage()
                )
                .findFirst()
                .map(GeneralInfo::getContent)
                .orElse(null);
    }

    default String contactTypeToString(ContactType contactType) {
        return contactType.getContactType();
    }

}
