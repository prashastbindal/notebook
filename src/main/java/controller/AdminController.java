package controller;

import dao.CommentDao;
import dao.CourseDao;
import dao.DBBuilder;
import dao.NoteDao;
import fileserver.FileServer;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;

import java.util.List;
import java.util.stream.Collectors;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;
import static io.javalin.apibuilder.ApiBuilder.path;

public class AdminController extends Controller {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    public AdminController(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    /**
     * Setup DAO.
     */
    @Override
    void init() {
        this.courseDao = new CourseDao(this.sql2o);
        this.noteDao = new NoteDao(this.sql2o);
        this.commentDao = new CommentDao(this.sql2o);
    }

    /**
     * Register the handled endpoints with Javalin.
     */
    @Override
    void register() {

        app.routes(() -> {
            path("/admin", () -> {
                get(this::adminPageHandler);
                post("reset", this::resetDatabase);
                post("insertTestData", this::insertTestData);
                post("resetNotes", this::resetNotes);
                post("resetComments", this::resetComments);
            });
        });

    }

    /**
     * Handler for the admin page.
     *
     * @param ctx request context
     */
    private void adminPageHandler(Context ctx) {

        List<Course> courseList = this.courseDao.findAll();
        List<Note> noteList = this.noteDao.findAll();
        List<Comment> commentList = this.commentDao.findAll();

        class NoteInfo {
            int noteId, courseId;
            String noteName, courseName, author;
            public NoteInfo(Note n) {
                this.noteId = n.getId();
                this.courseId = n.getCourseId();
                this.noteName = n.getTitle();
                this.author = n.getCreator();
                this.courseName = courseDao.findCourse(n.getCourseId()).getName();
            }
        }

        class CommentInfo {
            int commentId, noteId, courseId;
            String text, noteName, courseName, author;
            public CommentInfo(Comment c) {
                this.commentId = c.getId();
                this.noteId = c.getNoteId();
                this.text = c.getText();
                if (this.text.length() > 25) {
                    this.text = this.text.substring(0, 25) + "...";
                }
                this.author = c.getCreator();
                Note n = noteDao.findNote(c.getNoteId());
                this.noteName = n.getTitle();
                Course course = courseDao.findCourse(n.getCourseId());
                this.courseId = course.getId();
                this.courseName = course.getName();
            }
        }

        List<NoteInfo> noteInfoList = noteList.stream()
                .map(n -> new NoteInfo(n))
                .collect(Collectors.toList());
        List<CommentInfo> commentInfoList = commentList.stream()
                .map(c -> new CommentInfo(c))
                .collect(Collectors.toList());

        ctx.render(
            "/templates/admin.mustache",
            TemplateUtil.model(
                "courseList", courseList,
                "noteList", noteInfoList,
                "commentList", commentInfoList
            )
        );
    }

    /**
     * Handler for reset database button.
     *
     * @param ctx request context
     */
    private void resetDatabase(Context ctx) {
        DBBuilder.createTables(this.sql2o, true);
        FileServer.getFileServer().reset();
        ctx.redirect("/admin");
    }

    /**
     * Handler for insert test data button.
     *
     * @param ctx request context
     */
    private void insertTestData(Context ctx) {
        DBBuilder.createTestData(sql2o, false);
        ctx.redirect("/admin");
    }

    /**
     * Handler for resetting notes.
     *
     * @param ctx request context
     */
    private void resetNotes(Context ctx) {
        for (Note note : this.noteDao.findAll()) {
            this.noteDao.remove(note);
        }
        ctx.redirect("/admin");
    }

    /**
     * Handler for resetting comments.
     *
     * @param ctx request context
     */
    private void resetComments(Context ctx) {
        for (Comment comment : this.commentDao.findAll()) {
            this.commentDao.remove(comment);
        }
        ctx.redirect("/admin");
    }
}
