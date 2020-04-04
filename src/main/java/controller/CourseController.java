package controller;

import dao.CourseDao;
import dao.NoteDao;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;
import static io.javalin.apibuilder.ApiBuilder.*;

import java.util.List;

/**
 * Controller for course page with list of notes.
 */
public class CourseController extends Controller {

    CourseDao courseDao;
    NoteDao noteDao;

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    public CourseController(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new CourseDao(sql2o);
        this.noteDao = new NoteDao(sql2o);
    }

    @Override
    void register() {

        app.routes(() -> {
            path("courses/:courseId/notes", () -> {
                get(this::getNotes);
                get("json", this::getNotesJSON);
            });
            post("courses/:courseId/delete", this::deleteCourse);
            post("courses/addCourse", this::addCourse);
            get("courses/:courseId/notes/search/:key", this::searchNotesJSON);
            get("courses/:courseId/notes/searchName/:key", this::searchNotesNameJSON);
            get("courses/:courseId/notes/searchContent/:key", this::searchNotesContentJSON);
            get("courses/:courseId/notes/searchCreator/:key", this::searchNotesCreatorJSON);
            get("courses/:courseId/notes/searchDate/:key", this::searchNotesDateJSON);
        });

    }

    /**
     * Handler for course page with list of notes.
     *
     * @param ctx request context
     */
    public void getNotes(Context ctx) {
        Course course = this.findCourse(ctx);
        List<Note> notes = noteDao.findNoteWithCourseId(course.getId());

        ctx.render(
            "/templates/notes.mustache",
            TemplateUtil.model(
                "courseId", course.getId(),
                "courseName", course.getName(),
                "noteList", notes
            )
        );
    }

    /**
     * Handler for notes list JSON requests.
     *
     * @param ctx request context
     */
    public void getNotesJSON(Context ctx) {
        Course course = this.findCourse(ctx);
        List<Note> notes = noteDao.findNoteWithCourseId(course.getId());

        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    /**
     * Handler for the add course form.
     *
     * @param ctx request context
     */
    public void addCourse(Context ctx) {
        Course course = new Course(ctx.formParam("courseName"));
        courseDao.add(course);
    }

    /**
     * Handler for removing courses.
     *
     * @param ctx request context
     */
    public void deleteCourse(Context ctx) {
        Course course = this.findCourse(ctx);
        courseDao.remove(course);
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

    public void searchNotesJSON(Context ctx) {
        int courseId = Integer.parseInt(ctx.pathParam("courseId"));

        String searchKey = ctx.pathParam("key");
        List<Note> notes = noteDao.search(searchKey, courseId);
        List<Note> notes2 = noteDao.searchNotesWithName(searchKey, courseId);
        notes.addAll(notes2);
        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    public void searchNotesNameJSON(Context ctx) {
        int courseId = Integer.parseInt(ctx.pathParam("courseId"));

        String searchKey = ctx.pathParam("key");
        List<Note> notes = noteDao.searchNotesWithName(searchKey, courseId);
        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    public void searchNotesContentJSON(Context ctx) {
        int courseId = Integer.parseInt(ctx.pathParam("courseId"));

        String searchKey = ctx.pathParam("key");
        List<Note> notes = noteDao.search(searchKey, courseId);
        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    public void searchNotesCreatorJSON(Context ctx) {
        int courseId = Integer.parseInt(ctx.pathParam("courseId"));

        String searchKey = ctx.pathParam("key");
        List<Note> notes = noteDao.searchNotesByCreator(searchKey, courseId);
        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    public void searchNotesDateJSON(Context ctx) {
        int courseId = Integer.parseInt(ctx.pathParam("courseId"));

        String searchKey = ctx.pathParam("key");
        List<Note> notes = noteDao.searchNotesByDate(searchKey, courseId);
        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

}
