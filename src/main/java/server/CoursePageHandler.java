package server;

import dao.CourseDao;
import dao.NoteDao;
import dao.Sql2oCourseDao;
import dao.Sql2oNoteDao;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Course;
import model.Note;
import org.sql2o.Sql2o;

import java.util.List;

public class CoursePageHandler extends RequestHandler {

    CourseDao courseDao;
    NoteDao noteDao;

    CoursePageHandler(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new Sql2oCourseDao(sql2o);
        this.noteDao = new Sql2oNoteDao(sql2o);
    }

    @Override
    void register() {

        app.get("/courses/:id/notes", ctx -> {
            String courseId = ctx.pathParam("id");
            try {
                int cId = Integer.parseInt(courseId);
                Course course = courseDao.findCourse(cId);
                List<Note> notes = noteDao.findNoteWithCourseId(cId);
                if (notes.isEmpty()) {
                    ctx.json("Error 404 not found");
                } else {
                    ctx.render(
                        "/notes.mustache",
                        TemplateUtil.model(
                            "courseName", course.getName(),
                            "noteList", notes
                        )
                    );
                }
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

        app.get("/courses/:id/notes_json", ctx -> {
            String courseId = ctx.pathParam("id");
            int cId;
            List<Note> notes;
            try {
                cId = Integer.parseInt(courseId);
                notes = noteDao.findNoteWithCourseId(cId);
                if (notes.isEmpty()) {
                    ctx.json("Error 404 not found");
                } else {
                    ctx.json(notes);
                    ctx.status(200);
                    ctx.contentType("application/json");
                }
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

    }
}
