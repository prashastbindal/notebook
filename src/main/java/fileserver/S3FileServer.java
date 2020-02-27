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
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    public void upload(InputStream file, int courseId, int noteId) {
        String filepath = courseId + "/" + noteId + ".pdf";
        try {
            Boolean exists = s3Client.doesObjectExist(this.bucketName, filepath);
            if (!exists) {
                return;
            }
            PutObjectRequest uploadRequest = new PutObjectRequest(this.bucketName, filepath, file, new ObjectMetadata());
            s3Client.putObject(uploadRequest);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    public String getURL(int courseId, int noteId) {
        String objectKey = courseId + "/" + noteId + ".pdf";

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        try {
            Boolean exists = s3Client.doesObjectExist(this.bucketName, objectKey);
            if (!exists) {
                return null;
            }
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(this.bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}
