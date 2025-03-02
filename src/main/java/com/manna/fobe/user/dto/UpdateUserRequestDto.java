package com.manna.fobe.user.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String nickname;
    private String imageUri;
    private String introduce;
    private String hatId;
    private String handId;
    private String skinId;
    private String topId;
    private String faceId;
    private String bottomId;
    private String background;
}
