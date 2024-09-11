package com.imures.kaadbackend.specialization.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "specialization_page")
@Getter
@Setter
public class SpecializationPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @OneToOne(mappedBy = "specializationPage")
    private Specialization specialization;

}
