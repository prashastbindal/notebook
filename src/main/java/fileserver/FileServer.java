package fileserver;

import model.Note;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileServer {
    void upload(InputStream file, Note note);
    String getURL(Note note);

    static FileServer getFileServer() {
        String use_aws = System.getenv("AWS_ENABLE");
        FileServer fileServer;
        if (use_aws != null && use_aws.equals("TRUE")) {
            fileServer = new S3FileServer();
        } else {
            Path uploadFileLocation = Paths.get("./static/uploads/");
            fileServer = new LocalFileServer(uploadFileLocation.toString());
        }
        return fileServer;
    }
}
