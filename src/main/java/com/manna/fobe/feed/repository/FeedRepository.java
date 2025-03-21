package com.manna.fobe.feed.repository;

import com.manna.fobe.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedRepository extends JpaRepository<Feed, Integer> {
    Page<Feed> findAll(Pageable pageable);

    Page<Feed> findByCategoryId(Pageable pageable, int categoryId);

    Page<Feed> findByUserId(int userId, Pageable pageable);

    Optional<Feed> findById(Integer feedId);
}