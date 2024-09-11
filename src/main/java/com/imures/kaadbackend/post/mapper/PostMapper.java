package com.imures.kaadbackend.post.mapper;

import com.imures.kaadbackend.post.controller.response.PostResponse;
import com.imures.kaadbackend.post.entity.Post;
import com.imures.kaadbackend.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "contentLength", expression = "java(countWords(post.getContent()))")
    PostResponse fromEntityToResponse(Post post);

    default String map(User value){
        return value.getFirstName() + " " + value.getLastName();
    }

    default int countWords(String content) {
        if (content == null || content.isEmpty()) {
            return 0;
        }
        return content.trim().split("\\s+").length; // Splits by one or more whitespace characters
    }
}
