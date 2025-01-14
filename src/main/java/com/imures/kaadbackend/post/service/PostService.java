package com.imures.kaadbackend.post.service;

import com.imures.kaadbackend.post.controller.request.PostRequest;
import com.imures.kaadbackend.post.controller.response.PostResponse;
import com.imures.kaadbackend.post.entity.Post;
import com.imures.kaadbackend.post.mapper.PostMapper;
import com.imures.kaadbackend.post.repository.PostRepository;
import com.imures.kaadbackend.user.entity.User;
import com.imures.kaadbackend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::fromEntityToResponse)
                .sorted(Comparator.comparing(PostResponse::getCreatedAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse getById(Long id) {
        return postMapper.fromEntityToResponse(
                postRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", id)))
        );
    }

    @Transactional
    public PostResponse createPost(PostRequest postRequest, String author) {

        User user = userRepository.findUserByEmail(author)
                .orElseThrow(()-> new EntityNotFoundException(String.format("User %s not found", author)));

        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setAuthor(user);
        post.setCreatedAt(OffsetDateTime.now());

        return postMapper.fromEntityToResponse(postRepository.save(post));
    }

    @Transactional
    public PostResponse updatePost(Long id, PostRequest postRequest, String author) throws AccessDeniedException {

        Post post = checkForAuthor(id, author);

        Optional.ofNullable(postRequest.getContent()).ifPresent(post::setContent);

        return postMapper.fromEntityToResponse(postRepository.save(post));
    }

    @Transactional
    public void deletePost(Long id, String author) throws AccessDeniedException {
        Post post = checkForAuthor(id, author);

        postRepository.delete(post);
    }

    private Post checkForAuthor(Long id, String author) throws AccessDeniedException {
        User user = userRepository.findUserByEmail(author)
                .orElseThrow(()-> new EntityNotFoundException(String.format("User %s not found", author)));

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", id)));

        if(!user.equals(post.getAuthor())) {
            throw new AccessDeniedException(String.format("Accuses denied for editing post with id %s", id));
        }
        return post;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findPaged(Pageable pageRequest) {
        Page<Post> posts = postRepository.findAll(pageRequest);
        return posts.map(postMapper::fromEntityToResponse);
    }

    @Transactional(readOnly = true)
    public byte[] getAuthorImage(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", postId)))
                .getAuthor().getBlogImage();
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> getByAuthor(String author, Pageable pageRequest) {
        User user = userRepository.findUserByEmail(author)
                .orElseThrow(()-> new EntityNotFoundException(String.format("User %s not found", author)));

        return postRepository.findByAuthor(user, pageRequest).map(postMapper::fromEntityToResponse);
    }
}
