package com.manna.fobe.config.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        log.info("Configuring S3 client for bucket: {} in region: {}", s3Properties.getBucketName(), s3Properties.getRegion());
        return S3Client.builder()
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        s3Properties.getCredentials().getAccessKey(),
                        s3Properties.getCredentials().getSecretKey()
                ))
                .region(Region.of(s3Properties.getRegion()))
                .build();
    }
}