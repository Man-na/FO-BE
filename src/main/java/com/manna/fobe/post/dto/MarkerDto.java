package com.manna.fobe.post.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkerDto {
    private Long id;
    private double latitude;
    private double longitude;
    private String color;
    private int score;
}