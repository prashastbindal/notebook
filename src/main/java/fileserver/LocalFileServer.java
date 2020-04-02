package fileserver;

import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;
import model.Note;

import java.io.File;
import java.io.InputStream;

/**
 * File server that hosts files locally.
 */
public class LocalFileServer implements FileServer {

    private String basepath;

    /**
     * Instantiates a new local file server.
     *
     * @param basepath the root path to store files
     */
    public LocalFileServer(String basepath) {
        this.basepath = basepath;
        if (! new File(basepath).exists()) {
            new File(basepath).mkdirs();
        }
    }

    /**
     * Upload a file to the file server.
     *
     * @param file file to upload
     * @param note note that the file belongs to
     */
    public void upload(InputStream file, Note note) {
        String path = this.basepath + "/" + note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        Javalin.log.info("Uploading " + path + " to local fileserver.");
        FileUtil.streamToFile(file, path);
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
        return this.basepath + "/" + note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
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

        String shortpath = note.getCourseId() + "/" + note.getId() + "." + note.getFiletype();
        String fullpath = this.basepath + "/" + shortpath;
        File file = new File(fullpath);

        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * Remove all files in the file server.
     */
    @Override
    public void reset() {
        File base = new File(this.basepath);
        for (File dir : base.listFiles()) {
            for (File file : dir.listFiles()) {
                Javalin.log.info(file.getAbsolutePath());
                file.delete();
            }
            dir.delete();
        }
    }

}
