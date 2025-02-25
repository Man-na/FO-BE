package com.manna.fobe.post.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Post;
import com.manna.fobe.post.service.PostService;
import com.manna.fobe.common.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final S3Utils s3Utils;

    // post 추가
    @PostMapping("/posts")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody CreatePostDto createPostDto, @RequestAttribute("userId") int userId) {
        Post createdPost = postService.createPost(createPostDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdPost)
                .statusCode(201)
                .resultMessage("post 추가 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // 내 마커 조회
    @GetMapping("/markers/my")
    public ResponseEntity<ResponseMessage> getMyMarkers(
            @RequestAttribute("userId") int userId
    ) {
        List<Post> markers = postService.getMyMarkers(userId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(markers)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 내 게시글 조회
    @GetMapping("/posts/my")
    public ResponseEntity<ResponseMessage> getPosts(
            @RequestAttribute("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Post> postsPage = postService.getMyPosts(userId, PageRequest.of(page - 1, size));

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(postsPage)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    // 개별 post 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getSinglePost(@PathVariable("id") int id) {

        Post post = postService.getSinglePost(id);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(post)
                .statusCode(200)
                .resultMessage("포스트 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping("/calendar")
    public ResponseEntity<ResponseMessage> getCalendarPosts(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestAttribute("userId") int userId
    ) {
        Map<Integer, List<Post>> calendarPosts = postService.getCalendarPosts(year, month, userId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(calendarPosts)
                .statusCode(200)
                .resultMessage("캘린더 포스트 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 이미지 업로드
    @PostMapping("/images")
    public ResponseEntity<ResponseMessage> uploadImages(@RequestParam("images") List<MultipartFile> files) throws IOException {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            imagePaths.add(s3Utils.uploadFile(file));
        }

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(imagePaths)
                .statusCode(200)
                .resultMessage("이미지 업로드 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
