package com.imures.kaadbackend.post.entity;

import com.imures.kaadbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

}
