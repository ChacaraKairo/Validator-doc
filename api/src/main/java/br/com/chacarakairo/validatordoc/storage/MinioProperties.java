package br.com.chacarakairo.validatordoc.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "validator.storage.minio")
public record MinioProperties(
    String endpoint,
    String accessKey,
    String secretKey,
    String bucket
) {
}
