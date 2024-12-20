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
    private Long id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private Specialization specialization;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GeneralInfo> content = new ArrayList<>();

    @Column
    private String imageName;

    @Lob
    @Column
    private byte[] imageData;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        content.add(generalInfo);
    }

}
