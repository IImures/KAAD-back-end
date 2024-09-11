package com.imures.kaadbackend.specialization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "specialization")
@Getter
@Setter
public class Specialization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "spec_page", referencedColumnName = "id")
    private SpecializationPage specializationPage;

    @Column(nullable = false)
    private String imgName;

    @Column(nullable = false)
    private String imgType;

    @Lob
    @Column(nullable = false)
    private byte[] imageData;

}

