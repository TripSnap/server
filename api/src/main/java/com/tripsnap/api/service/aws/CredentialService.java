package com.tripsnap.api.service.aws;

import lombok.Getter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Service
public class CredentialService {
    private final static ProfileCredentialsProvider credentialsProvider;
    static {
        credentialsProvider = ProfileCredentialsProvider.create("s3");
    }

    @Getter
    private final AwsCredentials awsS3Credentials;

    @Getter
    private final Region region = Region.AP_NORTHEAST_2;

    public CredentialService() {
        this.awsS3Credentials = credentialsProvider.resolveCredentials();
    }

    public AwsCredentialsProvider getAwsCredentialsProvider() {
        return credentialsProvider;
    }


}
