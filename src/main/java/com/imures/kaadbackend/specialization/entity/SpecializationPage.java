package com.imures.kaadbackend.specialization.entity;

import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "specialization_page")
@Getter
@Setter
public class SpecializationPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralInfo> content = new ArrayList<>();

    @Column(nullable = true)
    private String imageName;

    @Column(nullable = true)
    private byte[] imageData;

    @OneToOne(mappedBy = "specializationPage")
    private Specialization specialization;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        content.add(generalInfo);
    }

}
