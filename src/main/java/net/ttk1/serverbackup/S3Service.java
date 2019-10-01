package net.ttk1.serverbackup;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;

import java.io.File;

class S3Service {
    private final AmazonS3 s3Client;
    private final String bucket_name;
    private final String prefix;
    private final boolean overwrite;

    S3Service(String region, boolean overwrite, String bucket_name, String prefix, String access_key, String access_token) {
        this.overwrite = overwrite;
        this.prefix = prefix;
        this.bucket_name = bucket_name;
        AWSCredentials credentials = new BasicAWSCredentials(access_key, access_token);
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTPS);
        clientConfig.setConnectionTimeout(10_000);
        this.s3Client = AmazonS3ClientBuilder
                .standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withClientConfiguration(clientConfig)
                .build();
    }

    void upload(File backupFile) throws Exception {
        if (!this.overwrite) {
            ObjectListing objectListing = s3Client.listObjects(this.bucket_name, this.prefix + "/" + backupFile.getName());
            if (objectListing.getObjectSummaries().size() > 0) {
                throw new Exception("既にS3にファイルが存在します。");
            }
        }
        this.s3Client.putObject(this.bucket_name, this.prefix + "/" + backupFile.getName(), backupFile);
    }
}
