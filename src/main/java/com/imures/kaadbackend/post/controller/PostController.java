package com.imures.kaadbackend.post.controller;

import com.imures.kaadbackend.configuration.JwtService;
import com.imures.kaadbackend.post.controller.request.PostRequest;
import com.imures.kaadbackend.post.controller.response.PostResponse;
import com.imures.kaadbackend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPagedPosts(
            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    value = "limit",
                    required = false,
                    defaultValue = "20"
            ) int limit,
            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "id"
            ) String sortBy
    ) {
        Pageable pageRequest = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        return new ResponseEntity<>(postService.findPaged(pageRequest), HttpStatus.OK);
    }

    @GetMapping(path = "{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable Long postId
    ) {
        return new ResponseEntity<>(postService.getById(postId), HttpStatus.OK);
    }

    @GetMapping(path = "{postId}/author-image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPostAuthorImage(
            @PathVariable Long postId
    ){
        return new ResponseEntity<>(postService.getAuthorImage(postId), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody PostRequest postRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ){
        String author = jwtService.extractUsername(token.substring(7));
        return new ResponseEntity<>(postService.createPost(postRequest, author), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping(path = "{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestBody PostRequest postRequest,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws AccessDeniedException {
        String author = jwtService.extractUsername(token.substring(7));
        return new ResponseEntity<>(postService.updatePost(postId, postRequest, author), HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping(path = "{postId}")
    public ResponseEntity<PostResponse> deletePost(
            @PathVariable Long postId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ) throws AccessDeniedException {
        String author = jwtService.extractUsername(token.substring(7));
        postService.deletePost(postId, author);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping(path = "author")
    public ResponseEntity<Page<PostResponse>> getPostByAuthor(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token,
            @RequestParam(
                    value = "page",
                    required = false,
                    defaultValue = "0"
            ) int page,
            @RequestParam(
                    value = "limit",
                    required = false,
                    defaultValue = "20"
            ) int limit,
            @RequestParam(
                    value = "sort",
                    required = false,
                    defaultValue = "createdAt"
            ) String sortBy

    ) {
        Pageable pageRequest = PageRequest.of(page, limit, Sort.by(sortBy).descending());
        String author = jwtService.extractUsername(token.substring(7));
        return new ResponseEntity<>(postService.getByAuthor(author, pageRequest), HttpStatus.OK);
    }

}
