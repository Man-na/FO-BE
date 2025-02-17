package com.manna.fobe.post.service;

import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostDto createPostDto, int userId);

    List<Post> getMyPosts(int userId);

    Post getSinglePost(int postId);
}