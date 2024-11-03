package com.imures.kaadbackend.specialization.entity;

import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import jakarta.persistence.*;
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_page", referencedColumnName = "id")
    private SpecializationPage specializationPage;

    // Todo details -> specNames
    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralInfo> details;

    @Column(nullable = true)
    private String imgName;

    @Lob
    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        details.add(generalInfo);
    }

}

