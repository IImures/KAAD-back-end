package com.imures.kaadbackend.contact.repository;

import com.imures.kaadbackend.contact.entity.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactTypeRepository extends JpaRepository<ContactType, Long> {
}
