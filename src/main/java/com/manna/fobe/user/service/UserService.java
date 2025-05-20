package com.manna.fobe.user.service;

import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.dto.Tokens;
import com.manna.fobe.user.dto.UpdateUserRequestDto;
import com.manna.fobe.user.entity.User;

public interface UserService {
    User signup(SignupRequestDto createUserRequestDto);

    Tokens login(LoginRequestDto userCommonDto);

    Tokens refresh(String refreshToken);

    User getMyProfile(int userId);

    User updateProfile(int userId, UpdateUserRequestDto updateUserRequestDto);

    Tokens refresh(String accessToken, String refreshToken);
}