package com.manna.fobe.avatar.service;

import com.manna.fobe.common.exception.BizRuntimeException;
import com.manna.fobe.common.utils.S3Utils;
import com.manna.fobe.common.utils.SvgUtils;
import com.manna.fobe.user.dto.UpdateUserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvatarServiceImpl implements AvatarService {

    private final S3Utils s3Utils;

    @Override
    public String[] getAvatarList(String type) {
        return s3Utils.getAvatarUrls(type);
    }

    public String createAvatar(UpdateUserRequestDto dto) {
        try {
            BufferedImage baseImage = new BufferedImage(230, 230, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = baseImage.createGraphics();
            g2d.setBackground(new Color(255, 255, 255, 255));
            g2d.clearRect(0, 0, 230, 230);

            if (dto.getSkinId() != null && !dto.getSkinId().isEmpty()) {
                String skinUrl = getS3Url("assets/avatar/items/skins/" + dto.getSkinId() + ".svg");
                drawImageFromUrl(g2d, skinUrl);
            }

            String frameUrl = getS3Url("assets/avatar/default/frame.svg");
            drawImageFromUrl(g2d, frameUrl);

            if (dto.getHandId() != null && !dto.getHandId().isEmpty()) {
                String handUrl = getS3Url("assets/avatar/items/hands/" + dto.getHandId() + ".png");
                drawImageFromUrl(g2d, handUrl);
            }
            if (dto.getBottomId() != null && !dto.getBottomId().isEmpty()) {
                String bottomUrl = getS3Url("assets/avatar/items/bottoms/" + dto.getBottomId() + ".svg");
                drawImageFromUrl(g2d, bottomUrl);
            }
            if (dto.getTopId() != null && !dto.getTopId().isEmpty()) {
                String topUrl = getS3Url("assets/avatar/items/tops/" + dto.getTopId() + ".svg");
                drawImageFromUrl(g2d, topUrl);
            }
            if (dto.getFaceId() != null && !dto.getFaceId().isEmpty()) {
                String faceUrl = getS3Url("assets/avatar/items/faces/" + dto.getFaceId() + ".svg");
                drawImageFromUrl(g2d, faceUrl);
            }
            if (dto.getHatId() != null && !dto.getHatId().isEmpty()) {
                String hatUrl = getS3Url("assets/avatar/items/hats/" + dto.getHatId() + ".svg");
                drawImageFromUrl(g2d, hatUrl);
            }

            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(baseImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            MultipartFile multipartFile = new MockMultipartFile(
                    "avatar",
                    "avatar.png",
                    "image/png",
                    imageBytes
            );

            return s3Utils.uploadFile(multipartFile, "assets/profiles");
        } catch (IOException e) {
            log.error("아바타 생성 및 S3 업로드 중 IO 오류 발생", e);
            throw new BizRuntimeException("아바타 생성 및 업로드 중 오류가 발생했습니다.", e);
        }
    }

    private String getS3Url(String key) {
        return "https://" + s3Utils.getS3Properties().getBucketName() + ".s3." +
                s3Utils.getS3Properties().getRegion() + ".amazonaws.com/" + key;
    }

    private void drawImageFromUrl(Graphics2D g2d, String url) {
        try {
            BufferedImage layerImage;
            if (url.endsWith(".svg")) {
                layerImage = SvgUtils.readSvg(url);
            } else {
                layerImage = ImageIO.read(new java.net.URL(url));
            }
            if (layerImage != null) {
                g2d.drawImage(layerImage, 0, 0, 230, 230, null);
            } else {
                log.error("이미지를 로드할 수 없습니다: {}", url);
            }
        } catch (Exception e) {
            log.error("이미지 로드 실패: {}", url, e);
        }
    }
}
