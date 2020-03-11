package controller;

import dao.*;
import fileserver.FileServer;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class NoteController extends Controller {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;
    FileServer fileServer;

    public NoteController(Javalin app, Sql2o sql2o) {
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

        app.routes(() -> {
            path("/courses/:courseId/notes/:noteId", () -> {
                get(this::getNote);
                get("json", this::getNoteJSON);
                post("comment", this::addComment);
            });
            path("/courses/:courseId/notes", () -> {
                post(this::addNote);
            });
            path("/courses/:courseId/addNote", () -> {
                get(this::addNoteForm);
            });
        });

    }

    public void getNote(Context ctx) {
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
    }

    public void getNoteJSON(Context ctx) {
    }

    public void addComment(Context ctx) {
        String courseId = ctx.pathParam("courseId");
        String noteId = ctx.pathParam("noteId");
        try {
            int nId = Integer.parseInt(noteId);
            Comment comment = new Comment(
                nId,
                ctx.formParam("text"),
                ctx.formParam("creator")
            );
            commentDao.add(comment);
            ctx.redirect("/courses/".concat(courseId).concat("/notes/").concat(noteId).concat("/"));
        } catch (NumberFormatException e) {
            ctx.json("Error 404 not found");
        }
    }

    public void addNote(Context ctx) {
        String courseId = ctx.pathParam("courseId");
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
    }

    public void addNoteForm(Context ctx) {
        String courseId = ctx.pathParam("courseId");
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
    }

}
