package com.manna.fobe.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "s3")
public class S3Properties {
    private String bucketName;
    private String region;
    private Credentials credentials;

    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }
}
