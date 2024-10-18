package com.imures.kaadbackend.generalinfo.entity;

import com.imures.kaadbackend.language.entity.Language;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class GeneralInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


}
