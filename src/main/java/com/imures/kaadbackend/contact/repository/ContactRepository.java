package com.imures.kaadbackend.contact.repository;

import com.imures.kaadbackend.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findALlByContactType_IdIn(Pageable pageable, Set<Long> ids);

    Optional<Contact> findByUUID(String uuid);
}
