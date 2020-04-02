package fileserver;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import io.javalin.Javalin;
import kotlin.NotImplementedError;
import model.Note;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * File server that hosts files on AWS S3.
 */
public class S3FileServer implements FileServer {

    private String bucketName;
    private AmazonS3 s3Client;

    /**
     * Instantiates a new S3 file server.
     */
    public S3FileServer() {
        bucketName = System.getenv("AWS_S3_BUCKET");

        AWSCredentialsProvider awsCredentials = new EnvironmentVariableCredentialsProvider();
        s3Client = AmazonS3ClientBuilder.standard()
                                        .withCredentials(awsCredentials)
                                        .withRegion(Regions.US_EAST_1)
                                        .build();
    }

    /**
     * Upload a file to the file server.
     *
     * @param file file to upload
     * @param note note that the file belongs to
     */
    public void upload(InputStream file, Note note) {
        String filepath = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();

        Javalin.log.info("Uploading " + filepath + " to S3 fileserver.");
        try {
            Boolean exists = s3Client.doesObjectExist(this.bucketName, filepath);
            if (exists) {
                Javalin.log.info(filepath + " already exists in S3 fileserver.");
                return;
            }

            PutObjectRequest uploadRequest = new PutObjectRequest(
                this.bucketName,
                filepath,
                file,
                new ObjectMetadata()
            );

            ObjectMetadata metadata = new ObjectMetadata();
            if (note.getFiletype().equals("pdf")) {
                metadata.setContentType("application/pdf");
            } else if (note.getFiletype().equals("html")) {
                metadata.setContentType("text/html");
            }
            uploadRequest.setMetadata(metadata);

            s3Client.putObject(uploadRequest);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the URL of the file associated with a note.
     *
     * @param note the note
     * @return URL of the file
     */
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
            return url.toString();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the path to a local copy of the file associated with a note.
     *
     * @param note the note
     * @return path to the file
     */
    @Override
    public String getLocalFile(Note note) {
        if (note.getFiletype().equals("none")) {
            return null;
        }

        String objectKey = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        if (!s3Client.doesObjectExist(this.bucketName, objectKey)) {
            Javalin.log.info("Could not find " + objectKey + " in S3 fileserver.");
            return null;
        }

        String tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile(
                "c" + note.getCourseId() + "n" + note.getId() + "-", "." + note.getFiletype()
            ).toString();
            Javalin.log.info("S3 temp file: " + tempFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        File tempFile = new File(tempFilePath);
        s3Client.getObject(new GetObjectRequest(this.bucketName, objectKey), tempFile);
        return tempFilePath;
    }

    /**
     * Remove the file associated with a note.
     *
     * @param note the note
     */
    @Override
    public void remove(Note note) {
        if (note.getFiletype().equals("none")) {
            return;
        }

        String objectKey = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        s3Client.deleteObject(this.bucketName, objectKey);
    }

    /**
     * Remove all files in the file server.
     */
    @Override
    public void reset() {
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<DeleteObjectsRequest.KeyVersion>();

        ObjectListing objects = s3Client.listObjects(this.bucketName);
        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
            keys.add(new DeleteObjectsRequest.KeyVersion(objectSummary.getKey()));
        }

        DeleteObjectsRequest req = new DeleteObjectsRequest(this.bucketName);
        req.setKeys(keys);
        s3Client.deleteObjects(req);
    }

}
