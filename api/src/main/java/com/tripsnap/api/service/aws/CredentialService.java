package com.tripsnap.api.service.aws;

import lombok.Getter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

@Service
public class CredentialService {
    private final static ProfileCredentialsProvider credentialsProvider;
    static {
        credentialsProvider = ProfileCredentialsProvider.create("s3");
    }

    @Getter
    private final AwsCredentials awsS3Credentials;

    public CredentialService() {
        this.awsS3Credentials = credentialsProvider.resolveCredentials();
    }
}
