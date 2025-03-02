package com.manna.fobe.marker.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Long id;
    private String uri;
}