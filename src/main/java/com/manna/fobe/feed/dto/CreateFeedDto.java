package com.manna.fobe.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedDto {
    private String title;
    private String description;
    private int userId;
    private int categoryId;
}