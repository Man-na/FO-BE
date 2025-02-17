package com.manna.fobe.post.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.post.dto.CreatePostDto;
import com.manna.fobe.post.entity.Marker;
import com.manna.fobe.post.entity.Post;
import com.manna.fobe.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

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
        List<Marker> markers = postService.getMyMarkers(userId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(markers)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

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

    // 이미지 업로드
    @PostMapping("/images")
    public ResponseEntity<ResponseMessage> uploadImage(@RequestParam("file") MultipartFile file) throws IOException, IOException {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get("assets/images/", filename);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data("assets/images/" + filename)
                .statusCode(200)
                .resultMessage("이미지 업로드 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
