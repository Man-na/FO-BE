package com.manna.fobe.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Date date;
    private String description;
    private int userId;
    private MarkerDto marker;
    private List<ImageDto> imageUris;
}