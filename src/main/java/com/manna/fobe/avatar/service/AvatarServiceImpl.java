package com.manna.fobe.avatar.service;

import com.manna.fobe.common.utils.S3Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
    private final S3Utils s3Utils;

    @Override
    public String[] getAvatarList(String type) {
        return s3Utils.getAvatarUrls(type);
    }
}
