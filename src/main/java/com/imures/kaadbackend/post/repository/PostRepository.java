package com.imures.kaadbackend.post.repository;

import com.imures.kaadbackend.post.entity.Post;
import com.imures.kaadbackend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthor(User user, Pageable pageable);
}
