package br.com.chacarakairo.validatordoc.storage;

import java.io.InputStream;

public interface ObjectStorage {
    StoredObject put(String objectKey, String mediaType, long size, InputStream inputStream);
    void delete(String objectKey);
}
