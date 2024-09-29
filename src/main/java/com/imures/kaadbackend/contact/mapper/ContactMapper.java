package com.imures.kaadbackend.contact.mapper;

import com.imures.kaadbackend.contact.controller.request.ContactRequest;
import com.imures.kaadbackend.contact.controller.response.ContactResponse;
import com.imures.kaadbackend.contact.entity.Contact;
import com.imures.kaadbackend.contact.entity.ContactType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    Contact fromRequestToEntity(ContactRequest contactRequest);
    ContactResponse fromEntityToResponse(Contact contact);

    default String contactTyprToString(ContactType contactType) {
        return contactType.getContactType();
    }
}
