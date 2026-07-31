package br.com.chacarakairo.validatordoc.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import org.springframework.stereotype.Component;

@Component
public class MinioObjectStorage implements ObjectStorage {

    private final MinioClient client;
    private final MinioProperties properties;

    public MinioObjectStorage(MinioClient client, MinioProperties properties) {
        this.client = client;
        this.properties = properties;
    }

    @Override
    public StoredObject put(String objectKey, String mediaType, long size, InputStream inputStream) {
        try {
            ensureBucket();
            client.putObject(PutObjectArgs.builder()
                .bucket(properties.bucket())
                .object(objectKey)
                .contentType(mediaType)
                .stream(inputStream, size, -1)
                .build());
            return new StoredObject(properties.bucket(), objectKey);
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível armazenar o arquivo.", exception);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                .bucket(properties.bucket())
                .object(objectKey)
                .build());
        } catch (Exception exception) {
            throw new IllegalStateException("Não foi possível remover o arquivo.", exception);
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(properties.bucket()).build());
        if (!exists) {
            client.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
        }
    }
}
