package com.imures.kaadbackend.specialization.entity;

import com.imures.kaadbackend.contact.entity.Contact;
import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "specialization")
@Getter
@Setter
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "specialization", fetch = FetchType.LAZY, orphanRemoval = true)
    private SpecializationPage specializationPage;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralInfo> specNames;

    @Column(nullable = true)
    private String imgName;

    @Lob
    @Column(nullable = true)
    @EqualsAndHashCode.Exclude
    private byte[] imageData;

    @Column(nullable = false)
    private boolean isHidden = false;

    @OneToMany(mappedBy = "specialization", fetch = FetchType.LAZY)
    private List<Contact> contacts;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        specNames.add(generalInfo);
    }

}

