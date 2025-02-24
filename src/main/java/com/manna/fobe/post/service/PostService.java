package com.manna.fobe.post.service;

import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PostService {
    Post createPost(CreatePostDto createPostDto, int userId);

    List<Post> getMyMarkers(int userId);

    Post getSinglePost(int postId);

    Page<Post> getMyPosts(int userId, Pageable pageable);

    Map<Integer, List<Post>> getCalendarPosts(int year, int month, int userId);
}