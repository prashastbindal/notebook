package fileserver;

import model.Note;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The interface for file servers.
 */
public interface FileServer {

    /**
     * Upload a file to the file server.
     *
     * @param file file to upload
     * @param note note that the file belongs to
     */
    void upload(InputStream file, Note note);

    /**
     * Get the URL of the file associated with a note.
     *
     * @param note the note
     * @return URL of the file
     */
    String getURL(Note note);

    /**
     * Get the path to a local copy of the file associated with a note.
     *
     * @param note the note
     * @return path to the file
     */
    String getLocalFile(Note note);

    /**
     * Remove the file associated with a note.
     *
     * @param note the note
     */
    void remove(Note note);

    /**
     * Remove all files in the file server.
     */
    void reset();

    /**
     * Use environment flags to get the correct file server.
     *
     * @return the file server
     */
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
