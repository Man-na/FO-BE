package com.manna.fobe.user.service;

import com.manna.fobe.user.dto.LoginRequestDto;
import com.manna.fobe.user.dto.SignupRequestDto;
import com.manna.fobe.user.entity.User;

public interface UserService {
    User signup(SignupRequestDto createUserRequestDto);

    String login(LoginRequestDto userCommonDto);
}