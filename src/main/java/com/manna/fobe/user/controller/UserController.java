package com.manna.fobe.user.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.dto.UpdateUserRequestDto;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody SignupRequestDto signupRequestDto) {
        User createdUser = userService.signup(signupRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(201)
                .resultMessage("회원가입 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(
            @RequestBody LoginRequestDto loginRequestDto
    ) {
        Tokens tokens = userService.login(loginRequestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(tokens)
                .statusCode(200)
                .resultMessage("Login successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // refreshToken
    @GetMapping("/refresh")
    public ResponseEntity<ResponseMessage> refreshToken(
            @RequestHeader("Authorization") String refreshToken
    ) {
        Tokens tokens = userService.refresh(refreshToken);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(tokens)
                .statusCode(200)
                .resultMessage("Token refreshed")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        String refreshToken = authHeader != null ? authHeader.replace("Bearer ", "") : null;

        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Logout successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

    // 프로필 조회
    @GetMapping("/me")
    public ResponseEntity<ResponseMessage> getMyProfile(
            @RequestAttribute("userId") int userId
    ) {
        User user = userService.getMyProfile(userId);

        ResponseMessage response = ResponseMessage.builder()
                .data(user)
                .statusCode(200)
                .resultMessage("User profile retrieved")
                .build();

        return ResponseEntity.ok(response);
    }

    // 프로필 수정
    @PatchMapping("/me")
    public ResponseEntity<ResponseMessage> editProfile(
            @RequestAttribute("userId") int userId,
            @RequestBody UpdateUserRequestDto updateRequestDto
    ) {
        User updatedUser = userService.updateProfile(userId, updateRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(updatedUser)
                .statusCode(200)
                .resultMessage("Profile updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}
