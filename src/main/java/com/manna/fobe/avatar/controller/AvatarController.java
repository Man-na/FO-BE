package com.manna.fobe.avatar.controller;

import com.manna.fobe.avatar.service.AvatarService;
import com.manna.fobe.common.dto.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @GetMapping("/{type}")
    public ResponseEntity<ResponseMessage> getAvatarList(
            @PathVariable("type") String type
    ) {
        String[] validTypes = {"tops", "bottoms", "faces", "hands", "hats", "skins"};
        if (!Arrays.asList(validTypes).contains(type)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "유효하지 않은 경로입니다: " + type
            );
        }

        String[] avatarList = avatarService.getAvatarList(type);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(avatarList)
                .statusCode(200)
                .resultMessage("Avatar list retrieved successfully")
                .build();

        return ResponseEntity.ok(responseMessage);
    }
}
