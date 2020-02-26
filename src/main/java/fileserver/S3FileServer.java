package fileserver;

import java.io.InputStream;

public class S3FileServer implements FileServer {

    public void upload(InputStream file, int courseId, int noteId) {
        throw new UnsupportedOperationException();
    }

    public String getURL(int courseId, int noteId) {
        throw new UnsupportedOperationException();
    }

}
