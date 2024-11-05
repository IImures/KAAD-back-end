package com.imures.kaadbackend.post.controller.response;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PostResponse {
    private Long id;
    private String author;
    private Long authorId;
    private String content;
    private OffsetDateTime createdAt;
    private Integer contentLength;
}
