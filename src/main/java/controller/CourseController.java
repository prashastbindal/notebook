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

public class CourseController extends Controller {

    CourseDao courseDao;
    NoteDao noteDao;

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
        });

    }

    public void getNotes(Context ctx) {
        Course course = this.findCourse(ctx);
        List<Note> notes = noteDao.findNoteWithCourseId(course.getId());

        ctx.render(
            "/notes.mustache",
            TemplateUtil.model(
                "courseName", course.getName(),
                "noteList", notes
            )
        );
    }

    public void getNotesJSON(Context ctx) {
        Course course = this.findCourse(ctx);
        List<Note> notes = noteDao.findNoteWithCourseId(course.getId());

        ctx.json(notes);
        ctx.status(200);
        ctx.contentType("application/json");
    }

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

}
