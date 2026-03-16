package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Service
public class S3Service {

    private final S3Client s3;
    private final String bucket;

    public S3Service(@Value("${aws.s3.region}") String region,
                     @Value("${aws.s3.bucket}") String bucket) {
        this.bucket = bucket;
        this.s3 = S3Client.builder().region(Region.of(region)).build();
    }

    public String uploadFile(String key, byte[] content, String contentType) {
        s3.putObject(PutObjectRequest.builder()
                .bucket(bucket).key(key).contentType(contentType)
                .build(), RequestBody.fromBytes(content));
        return "Uploaded: " + key;
    }

    public byte[] downloadFile(String key) {
        return s3.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucket).key(key)
                .build()).asByteArray();
    }

    public List<String> listFiles(String prefix) {
        return s3.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucket).prefix(prefix)
                .build()).contents().stream().map(S3Object::key).toList();
    }

    public String deleteFile(String key) {
        s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucket).key(key)
                .build());
        return "Deleted: " + key;
    }
}
