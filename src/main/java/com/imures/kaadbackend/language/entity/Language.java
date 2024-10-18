package com.imures.kaadbackend.language.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private Boolean defaultLanguage;

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    private byte[] imageData;

}
