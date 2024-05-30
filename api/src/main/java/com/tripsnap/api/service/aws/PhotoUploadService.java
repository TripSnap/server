package com.tripsnap.api.service.aws;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PhotoUploadService {
    private final CredentialService credentialService;


    public String requestPresignedUrl(String bucketName, String filename) {
        return requestPresignedUrl(bucketName, filename, Map.of());
    }

    public String requestPresignedUrl(String bucketName, String keyName, Map<String, String> metadata) {
        try (
              S3Presigner presigner = S3Presigner.builder()
                      .credentialsProvider(credentialService.getAwsCredentialsProvider())
                      .region(credentialService.getRegion())
                      .build();
        ) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .metadata(metadata)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))  // The URL expires in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();


            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
}
