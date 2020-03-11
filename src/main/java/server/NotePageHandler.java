package server;

import dao.*;
import fileserver.FileServer;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;

import java.util.List;

public class NotePageHandler extends RequestHandler {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;
    FileServer fileServer;

    NotePageHandler(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new Sql2oCourseDao(sql2o);
        this.noteDao = new Sql2oNoteDao(sql2o);
        this.commentDao = new Sql2oCommentDao(sql2o);
        this.fileServer = FileServer.getFileServer();
    }

    @Override
    void register() {

        app.get("/courses/:courseId/notes/:noteId", ctx -> {
            String courseId = ctx.pathParam("courseId");
            String noteId = ctx.pathParam("noteId");
            int cId, nId;
            try {
                cId = Integer.parseInt(courseId);
                nId = Integer.parseInt(noteId);
                Course course = courseDao.findCourse(cId);
                Note note = noteDao.findNote(nId);
                List<Comment> comments = commentDao.findCommentWithNoteId(nId);
                if (note == null || course == null) {
                    ctx.json("Error 404 not found");
                } else {
                    String filepath = fileServer.getURL(note);
                    Boolean showfile = (filepath != null);
                    ctx.render(
                        "/note.mustache",
                        TemplateUtil.model(
                            "courseName", course.getName(),
                            "noteName", note.getTitle(),
                            "creatorName", note.getCreator(),
                            "filepath", filepath,
                            "showContent", showfile,
                            "commentList", comments
                        )
                    );
                }
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

    }
}
