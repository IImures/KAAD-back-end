package com.imures.kaadbackend.teammembder.entity;

import com.imures.kaadbackend.generalinfo.entity.GeneralInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @OneToMany(cascade = CascadeType.ALL)
    private List<GeneralInfo> descriptions;

    @Column
    private Long priority;

    @Column(nullable = false)
    private String imgName;

    @Lob
    @Column(nullable = false)
    private byte[] imageData;

    public void addGeneralInfo(GeneralInfo generalInfo) {
        descriptions.add(generalInfo);
    }

}
