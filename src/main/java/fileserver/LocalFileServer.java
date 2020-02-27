package fileserver;

import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;

import java.io.File;
import java.io.InputStream;

public class LocalFileServer implements FileServer {

    private String basepath;

    public LocalFileServer(String basepath) {
        this.basepath = basepath;
        if (! new File(basepath).exists()) {
            new File(basepath).mkdirs();
        }
    }

    public void upload(InputStream file, int courseId, int noteId) {
        String path = this.basepath + "/" + courseId + "/" + noteId + ".pdf";
        Javalin.log.info("Saving uploaded file to: " + path);
        FileUtil.streamToFile(file, path);
    }

    public String getURL(int courseId, int noteId) {
        String fullpath = this.basepath + "/" + courseId + "/" + noteId + ".pdf";
        String urlpath = "/uploads/" + courseId + "/" + noteId + ".pdf";
        if (new File(fullpath).exists()) {
            return urlpath;
        } else {
            return null;
        }
    }

}