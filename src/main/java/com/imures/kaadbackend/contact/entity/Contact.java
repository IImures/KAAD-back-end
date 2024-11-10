package com.imures.kaadbackend.contact.entity;

import com.imures.kaadbackend.language.entity.Language;
import com.imures.kaadbackend.specialization.entity.Specialization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity(name = "contact")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column
    private String phoneNumber;

    @Column
    private String email;

    @ManyToOne
    private Specialization specialization;

    @ManyToOne
    private Language language;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private boolean resolved;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private ContactType contactType;

    @Column(nullable = false)
    private String UUID;

}
