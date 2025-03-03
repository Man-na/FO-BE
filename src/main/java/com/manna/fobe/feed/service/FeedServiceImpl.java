package com.manna.fobe.feed.service;

import com.manna.fobe.feed.dto.CreateFeedDto;
import com.manna.fobe.feed.entity.Feed;
import com.manna.fobe.feed.repository.FeedRepository;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Feed createFeed(CreateFeedDto createFeedDto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Feed feed = Feed.builder()
                .title(createFeedDto.getTitle())
                .description(createFeedDto.getDescription())
                .author(user)
                .build();

        return feedRepository.save(feed);
    }

    @Override
    public Page<Feed> getFeeds(Pageable pageable) {
        return feedRepository.findAll(pageable);
    }

    @Override
    public Feed getSingleFeed(int feedId) {
        return feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("Feed not found"));
    }

    @Override
    public Page<Feed> getMyFeeds(int userId, Pageable pageable) {
        return feedRepository.findByUserId(userId, pageable);
    }
}