package fileserver;

import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;
import model.Note;

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

    public void upload(InputStream file, Note note) {
        String path = this.basepath + "/" + note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        Javalin.log.info("Uploading " + path + " to local fileserver.");
        FileUtil.streamToFile(file, path);
    }

    public String getURL(Note note) {
        if (note.getFiletype().equals("none")) {
            return null;
        }

        String shortpath = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        String fullpath = this.basepath + "/" + shortpath;
        String urlpath = "/uploads/" + shortpath;

        if (new File(fullpath).exists()) {
            Javalin.log.info("Retrieving " + fullpath + " from local fileserver.");
            return urlpath;
        } else {
            Javalin.log.info("Could not find " + fullpath + " in local fileserver.");
            return null;
        }
    }

}
