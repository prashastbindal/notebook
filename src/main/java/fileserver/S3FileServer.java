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
import io.javalin.Javalin;
import model.Note;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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

    public void upload(InputStream file, Note note) {
        String filepath = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();

        Javalin.log.info("Uploading " + filepath + " to S3 fileserver.");
        try {
            Boolean exists = s3Client.doesObjectExist(this.bucketName, filepath);
            if (exists) {
                Javalin.log.info(filepath + " already exists in S3 fileserver.");
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

    public String getURL(Note note) {
        if (note.getFiletype().equals("none")) {
            return null;
        }

        String objectKey = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();

        Javalin.log.info("Retrieving " + objectKey + " from S3 fileserver.");

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        try {
            Boolean exists = s3Client.doesObjectExist(this.bucketName, objectKey);
            if (!exists) {
                Javalin.log.info("Could not find " + objectKey + " in S3 fileserver.");
                return null;
            }
            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(this.bucketName, objectKey)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

            Javalin.log.info("Found " + url.toString() + " in S3 fileserver.");
            return URLEncoder.encode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException | SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }

}
