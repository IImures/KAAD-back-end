package com.imures.kaadbackend.teammembder.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "team_member")
@Getter
@Setter
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column
    private String email;

    @Column
    private String phone;

    @Column
    private Long priority;

    @Column(nullable = false)
    private String imgName;

    @Column(nullable = false)
    private String imgType;

    @Lob
    @Column(nullable = false)
    private byte[] imageData;


}
