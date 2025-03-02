package com.manna.fobe.avatar.service;

import com.manna.fobe.user.dto.UpdateUserRequestDto;

public interface AvatarService {

    String[] getAvatarList(String type);

    String createAvatar(UpdateUserRequestDto userRequestDto);
}