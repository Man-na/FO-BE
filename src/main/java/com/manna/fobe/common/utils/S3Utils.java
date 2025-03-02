package com.manna.fobe.common.utils;

import com.manna.fobe.config.s3.S3Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Utils {

    private final S3Client s3Client;
    @Getter
    private final S3Properties s3Properties;

    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, "assets/images");
    }

    public String uploadFile(MultipartFile file, String prefix) throws IOException {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String key = prefix + "/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucketName())
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                key);
    }

    public String[] getAvatarUrls(String type) {
        String prefix = "assets/avatar/" + type + "/";

        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Properties.getBucketName())
                .prefix(prefix)
                .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        List<String> avatarUrls = response.contents().stream()
                .filter(s3Object -> s3Object.key().endsWith(".png"))
                .map(s3Object -> String.format(
                        "https://%s.s3.%s.amazonaws.com/%s",
                        s3Properties.getBucketName(),
                        s3Properties.getRegion(),
                        s3Object.key()
                ))
                .toList();

        return avatarUrls.toArray(new String[0]);
    }

}