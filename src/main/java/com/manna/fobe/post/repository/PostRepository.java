package com.manna.fobe.post.repository;

import com.manna.fobe.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUserId(int userId);
    Page<Post> findByUserId(int userId, Pageable pageable);
    Post findById(int postId);
}