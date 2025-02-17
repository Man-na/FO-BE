package com.manna.fobe.post.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String uri;
}