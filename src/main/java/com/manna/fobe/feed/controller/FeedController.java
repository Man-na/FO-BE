package com.manna.fobe.feed.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.feed.dto.CreateFeedDto;
import com.manna.fobe.feed.entity.Feed;
import com.manna.fobe.feed.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    
    // 피드 추가
    @PostMapping("/")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody CreateFeedDto createFeedDto, @RequestAttribute("userId") int userId) {
        Feed createdFeed = feedService.createFeed(createFeedDto, userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdFeed)
                .statusCode(201)
                .resultMessage("피드 추가 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }


    // 피드 전체 조회
    @GetMapping("/feeds")
    public ResponseEntity<ResponseMessage> getMyFeeds(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Feed> feedsPage = feedService.getFeeds(PageRequest.of(page - 1, size));

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(feedsPage)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    // 내 피드 조회
    @GetMapping("/feeds/my")
    public ResponseEntity<ResponseMessage> getMyFeeds(
            @RequestAttribute("userId") int userId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<Feed> postsPage = feedService.getMyFeeds(userId, PageRequest.of(page - 1, size));

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(postsPage)
                .statusCode(200)
                .resultMessage("마커 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }


    // 개별 피드 조회
    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getSinglePost(@PathVariable("id") int id) {

        Feed feed = feedService.getSingleFeed(id);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(feed)
                .statusCode(200)
                .resultMessage("포스트 조회 성공")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
