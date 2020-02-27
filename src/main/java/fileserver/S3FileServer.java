package fileserver;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

public class S3FileServer implements FileServer {

    private String bucketName;
    private AmazonS3 s3Client;

    public S3FileServer() {
        bucketName = System.getenv("AWS_S3_BUCKET");

        AWSCredentialsProvider awsCredentials = new EnvironmentVariableCredentialsProvider();
        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(awsCredentials)
                .withRegion(Regions.DEFAULT_REGION)
                .build();
    }

    public void upload(InputStream file, int courseId, int noteId) {
        String filepath = courseId + "/" + noteId + ".pdf";
        PutObjectRequest uploadRequest = new PutObjectRequest(bucketName, filepath, file, new ObjectMetadata());
        s3Client.putObject(uploadRequest);
    }

    public String getURL(int courseId, int noteId) {
        String objectKey = courseId + "/" + noteId + ".pdf";

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

}
