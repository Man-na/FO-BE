package com.manna.fobe.post.service;

import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Image;
import com.manna.fobe.post.entity.Marker;
import com.manna.fobe.post.entity.Post;
import com.manna.fobe.post.repository.PostRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Post createPost(CreatePostDto createPostDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .address(createPostDto.getAddress())
                .date(createPostDto.getDate())
                .description(createPostDto.getDescription())
                .user(user)
                .marker(new Marker(createPostDto.getMarker().getId(), createPostDto.getMarker().getLatitude(), createPostDto.getMarker().getLongitude(), createPostDto.getMarker().getColor(), createPostDto.getMarker().getScore()))
                .imageUris(createPostDto.getImageUris().stream().map(img -> new Image(img.getId(), img.getUri(), null)).collect(Collectors.toList()))
                .build();
        return postRepository.save(post);
    }

    @Override
    public List<Post> getMyMarkers(int userId) {
        return postRepository.findByUserId(userId);
    }
}
