package server;

import dao.CourseDao;
import dao.NoteDao;
import dao.Sql2oCourseDao;
import dao.Sql2oNoteDao;
import fileserver.FileServer;
import io.javalin.Javalin;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NoteFormHandler extends RequestHandler {

    CourseDao courseDao;
    NoteDao noteDao;
    FileServer fileServer;

    NoteFormHandler(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new Sql2oCourseDao(sql2o);
        this.noteDao = new Sql2oNoteDao(sql2o);
        this.fileServer = FileServer.getFileServer();
    }

    @Override
    void register() {

        app.get("/courses/:id/addNote", ctx -> {
            String courseId = ctx.pathParam("id");
            try {
                int cId = Integer.parseInt(courseId);
                Course course = courseDao.findCourse(cId);
                ctx.render(
                    "/addNote.mustache",
                    TemplateUtil.model(
                        "courseName", course.getName()
                    )
                );
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

        app.post("/courses/:id/notes", ctx -> {
            String courseId = ctx.pathParam("id");
            try {
                int cId = Integer.parseInt(courseId);
                Note note = new Note(
                    cId,
                    ctx.formParam("title"),
                    ctx.formParam("creator"),
                    ctx.formParam("filetype")
                );
                noteDao.add(note);

                UploadedFile file = ctx.uploadedFile("file");
                if (note.getFiletype().equals("pdf")) {
                    fileServer.upload(file.getContent(), note);
                } else if (note.getFiletype().equals("html")) {
                    String text = ctx.formParam("text");
                    InputStream textstream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
                    fileServer.upload(textstream, note);
                }

                ctx.redirect("/courses/".concat(courseId).concat("/notes/"));
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

    }
}
