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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "specialization", fetch = FetchType.LAZY)
    private SpecializationPage specializationPage;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralInfo> specNames;

    @Column(nullable = true)
    private String imgName;

    @Lob
    @Column(nullable = true)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;

    @Column(nullable = false)
    private boolean isHidden = false;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        specNames.add(generalInfo);
    }

}

