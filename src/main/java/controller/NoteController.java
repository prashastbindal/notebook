package controller;

import dao.*;
import fileserver.FileServer;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;
import services.NotePublishService;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * Controller for note pages/endpoints.
 */
public class NoteController extends Controller {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;
    FileServer fileServer;
    SubscriptionDao subscriptionDao;
    NotePublishService notePublishService;

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    public NoteController(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new CourseDao(sql2o);
        this.noteDao = new NoteDao(sql2o);
        this.commentDao = new CommentDao(sql2o);
        this.fileServer = FileServer.getFileServer();
        this.subscriptionDao = new SubscriptionDao(sql2o);
        this.notePublishService = new NotePublishService(noteDao, subscriptionDao, courseDao);
    }

    /**
     * Register the handled endpoints with Javalin.
     */
    @Override
    void register() {

        app.routes(() -> {
            path("/courses/:courseId/notes/:noteId", () -> {
                get(this::getNoteView);
                get("json", this::getNoteJSON);
                post("checkUpvote", this::checkUpvoteNote);
                post("upvote", this::upvoteNote);
                post("comment", this::addComment);
                post("delete", this::deleteNote);
            });
            get("/courses/:courseId/notes-preview/:noteId", this::getNotePreview);
            path("/courses/:courseId/addNote", () -> {
                get(this::addNoteForm);
                post(this::addNote);
            });
            post("/notes/:noteId/delete", this::deleteNote);
            post("/comments/:commentId/delete", this::deleteComment);
        });

    }

    /**
     * Handler for note display page.
     *
     * @param ctx request context
     */
    public void getNoteView(Context ctx) {
        Course course = this.findCourse(ctx);
        Note note = this.findNote(ctx);

        List<Comment> comments = commentDao.findCommentWithNoteId(note.getId());

        String filepath = fileServer.getURL(note);
        Boolean fileExists = (filepath != null);

        Boolean showPDF = fileExists && note.getFiletype().equals("pdf");
        Boolean showHTML = fileExists && note.getFiletype().equals("html");

        ctx.render(
                "/templates/notePreview.mustache",
            TemplateUtil.model(
                "courseId", course.getId(),
                "noteId", note.getId(),
                "courseName", course.getName(),
                "noteName", note.getTitle(),
                "creatorName", note.getCreator(),
                "filepath", filepath,
                "showContent", fileExists,
                "showPDF", showPDF,
                "showHTML", showHTML,
                "commentList", comments,
                "dateCreated", note.getDate(),
                "numberOfUpvotes", note.getUpvotes()
            )
        );
    }

    /**
     * Handler for note preview page.
     *
     * @param ctx request context
     */
    public void getNotePreview(Context ctx) {
        Course course = this.findCourse(ctx);
        Note note = this.findNote(ctx);

        List<Comment> comments = commentDao.findCommentWithNoteId(note.getId());

        String filepath = fileServer.getURL(note);
        Boolean fileExists = (filepath != null);

        Boolean showPDF = fileExists && note.getFiletype().equals("pdf");
        Boolean showHTML = fileExists && note.getFiletype().equals("html");

        ctx.render(
            "/templates/notePreview.mustache",
            TemplateUtil.model(
                "courseId", course.getId(),
                "noteId", note.getId(),
                "courseName", course.getName(),
                "noteName", note.getTitle(),
                "creatorName", note.getCreator(),
                "filepath", filepath,
                "showContent", fileExists,
                "showPDF", showPDF,
                "showHTML", showHTML,
                "commentList", comments,
                "dateCreated", note.getDate(),
                "numberOfUpvotes", note.getUpvotes()
            )
        );
    }

    /**
     * Handler for note JSON requests.
     *
     * @param ctx request context
     */
    public void getNoteJSON(Context ctx) {
        Note note = this.findNote(ctx);

        ctx.json(note);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    /**
     * Handler for the add comment form.
     *
     * @param ctx request context
     */
    public void addComment(Context ctx) {
        Note note = this.findNote(ctx);

        Comment comment = new Comment(
            Integer.parseInt(ctx.formParam("parent-id")),
            note.getId(),
            ctx.formParam("text"),
            ctx.formParam("username")
        );
        if (comment.getParentId() == 0 || commentDao.findComment(comment.getParentId()).getNoteId() == note.getId()) {
            commentDao.add(comment);
        }
        ctx.redirect("/courses/" + note.getCourseId() + "/notes/" + note.getId());
    }

    /**
     * Handler for the upvote form.
     *
     * @param ctx request context
     */
    public void upvoteNote(Context ctx) {
        Note note = this.findNote(ctx);
        String upvotedCookieName = ctx.formParam("username")
                                    + "." + note.getCourseId()
                                    + "." + note.getId();
        if (!upvotedCookieName.equals("username")) {
            if (ctx.cookieStore(upvotedCookieName) == null || ctx.cookieStore(upvotedCookieName).equals("False")) {
                ctx.cookieStore(upvotedCookieName, "True");
                note.upvote();
            } else {
                ctx.cookieStore(upvotedCookieName, "False");
                note.unvote();
            }
        }
        noteDao.updateUpvotes(note);
    }

    /**
     * Check if a user can upvote a note.
     *
     * @param ctx request context
     */
    public void checkUpvoteNote(Context ctx) {
        String upvotedCookieName = ctx.formParam("username")
                + "." + ctx.pathParam("courseId")
                + "." + ctx.pathParam("noteId");
        if (ctx.cookieStore(upvotedCookieName) == null || ctx.cookieStore(upvotedCookieName).equals("False")) {
            ctx.result("true");
        } else {
            ctx.result("false");
        }
    }

    /**
     * Handler for removing comments.
     *
     * @param ctx request context
     */
    public void deleteComment(Context ctx) {
        Comment comment = this.findComment(ctx);
        commentDao.remove(comment);
    }

    /**
     * Handler for the add note form.
     *
     * @param ctx request context
     */
    public void addNote(Context ctx) {
        Course course = this.findCourse(ctx);

        Note note = new Note(
            course.getId(),
            ctx.formParam("title"),
            ctx.formParam("username"),
            ctx.formParam("filetype")
        );
        noteDao.add(note);

        UploadedFile file = ctx.uploadedFile("file");
        notePublishService.publishNote(note, file, ctx.formParam("text"));

        ctx.redirect("/courses/" + course.getId() + "/notes");
    }

    /**
     * Handler for displaying the add note form.
     *
     * @param ctx request context
     */
    public void addNoteForm(Context ctx) {
        Course course = this.findCourse(ctx);
        ctx.render(
            "/templates/addNote.mustache",
            TemplateUtil.model(
                "courseId", course.getId(),
                "courseName", course.getName()
            )
        );
    }

    /**
     * Handler for removing notes.
     *
     * @param ctx request context
     */
    public void deleteNote(Context ctx) {
        Note note = this.findNote(ctx);
        noteDao.remove(note);
    }

    /**
     * Get the course referenced in the given request.
     *
     * @param ctx request context
     */
    private Course findCourse(Context ctx) throws NotFoundResponse {
        String courseIdString = ctx.pathParam("courseId");

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdString);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse("Could not parse course ID in URL");
        }

        Course course = courseDao.findCourse(courseId);
        if (course == null) {
            throw new NotFoundResponse("Course not found.");
        }

        return course;
    }

    /**
     * Get the note referenced in the given request.
     *
     * @param ctx request context
     */
    private Note findNote(Context ctx) throws NotFoundResponse {
        String noteIdString = ctx.pathParam("noteId");

        int noteId;
        try {
            noteId = Integer.parseInt(noteIdString);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse("Could not parse note ID in URL");
        }

        Note note = noteDao.findNote(noteId);
        if (note == null) {
            throw new NotFoundResponse("Note not found.");
        }

        return note;
    }

    /**
     * Get the comment referenced in the given request.
     *
     * @param ctx request context
     */
    private Comment findComment(Context ctx) throws NotFoundResponse {
        String noteIdString = ctx.pathParam("commentId");

        int commentId;
        try {
            commentId = Integer.parseInt(noteIdString);
        } catch (NumberFormatException e) {
            throw new NotFoundResponse("Could not parse note ID in URL");
        }

        Comment comment = commentDao.findComment(commentId);
        if (comment == null) {
            throw new NotFoundResponse("Comment not found.");
        }

        return comment;
    }
}
