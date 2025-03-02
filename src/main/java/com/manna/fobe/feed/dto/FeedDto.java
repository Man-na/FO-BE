package com.manna.fobe.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedDto {
    private Long id;
    private double latitude;
    private double longitude;
    private String color;
    private int score;
}