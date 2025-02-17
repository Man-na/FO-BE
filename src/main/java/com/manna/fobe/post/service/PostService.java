package com.manna.fobe.post.service;

import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Marker;
import com.manna.fobe.post.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(CreatePostDto createPostDto, int userId);

    List<Marker> getMyMarkers(int userId);

    Post getSinglePost(int postId);
}