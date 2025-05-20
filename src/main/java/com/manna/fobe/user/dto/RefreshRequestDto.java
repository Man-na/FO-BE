package com.manna.fobe.user.dto;

import lombok.Data;

@Data
public class RefreshRequestDto {
    private String accessToken;
    private String refreshToken;
}