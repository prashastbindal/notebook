package fileserver;

import model.Note;

import java.io.InputStream;

public interface FileServer {
    void upload(InputStream file, Note note);
    String getURL(Note note);
}
