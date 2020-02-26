package fileserver;

import java.io.InputStream;

public interface FileServer {
    void upload(InputStream file, int courseId, int noteId);
    String getURL(int courseId, int noteId);
}
