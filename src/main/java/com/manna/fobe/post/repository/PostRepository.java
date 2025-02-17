package com.manna.fobe.post.repository;

import com.manna.fobe.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Post findById(int postId);
}