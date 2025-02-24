package com.manna.fobe.post.service;

import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Image;
import com.manna.fobe.post.entity.Post;
import com.manna.fobe.post.repository.PostRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Post createPost(CreatePostDto createPostDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .address(createPostDto.getAddress())
                .date(createPostDto.getDate())
                .description(createPostDto.getDescription())
                .user(user)
                .latitude(createPostDto.getLatitude())
                .longitude(createPostDto.getLongitude())
                .color(createPostDto.getColor())
                .score(createPostDto.getScore())
                .build();

        if (createPostDto.getImageUris() != null) {
            List<Image> images = createPostDto.getImageUris().stream()
                    .map(img -> new Image(null, img.getUri(), post))
                    .collect(Collectors.toList());
            post.setImages(images);
        }

        return postRepository.save(post);
    }

    @Override
    public List<Post> getMyMarkers(int userId) {
        return postRepository.findByUserId(userId);
    }

    @Override
    public Post getSinglePost(int postId) {
        return postRepository.findById(postId);
    }

    @Override
    public Page<Post> getMyPosts(int userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable);
    }

    @Override
    public Map<Integer, List<Post>> getCalendarPosts(int year, int month, int userId) {
        // 해당 연월의 시작일과 종료일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 해당 기간의 포스트 조회
        List<Post> posts = postRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        // 날짜별로 그룹화
        return posts.stream()
                .collect(Collectors.groupingBy(
                        post -> post.getDate().getDayOfMonth(),
                        Collectors.toList()
                ));
    }
}
