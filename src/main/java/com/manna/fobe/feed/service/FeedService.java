package com.manna.fobe.feed.service;

import com.manna.fobe.feed.dto.CreateFeedDto;
import com.manna.fobe.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface FeedService {
    Feed createFeed(CreateFeedDto createFeedDto, int userId);

    Page<Feed> getFeeds(Pageable pageable, int categoryId);

    Feed getSingleFeed(int feedId);

    Page<Feed> getMyFeeds(int userId, Pageable pageable);
}