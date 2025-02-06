package com.manna.fobe.user.controller;

import com.manna.fobe.common.dto.ResponseMessage;
import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.entity.User;
import com.manna.fobe.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody SignupRequestDto signupRequestDto) throws BadRequestException {
        User createdUser = userService.signup(signupRequestDto);

        ResponseMessage response = ResponseMessage.builder()
                .data(createdUser)
                .statusCode(201)
                .resultMessage("회원가입 성공")
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseMessage> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        Tokens tokens = userService.login(loginRequestDto);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .data(tokens)
                .statusCode(200)
                .resultMessage("Login successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

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

    @PostMapping("/logout")
    public ResponseEntity<ResponseMessage> logout() {
        ResponseMessage responseMessage = ResponseMessage.builder()
                .statusCode(200)
                .resultMessage("Logout successful")
                .build();

        return ResponseEntity.ok(responseMessage);
    }

}
