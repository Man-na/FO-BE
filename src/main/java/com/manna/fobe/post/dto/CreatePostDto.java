package com.manna.fobe.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDto {
    private Long id;
    private String title;
    private String address;
    private LocalDate date;
    private String description;
    private int userId;
    private double latitude;
    private double longitude;
    private int score;
    private String color;
    private List<ImageDto> imageUris;
}