package server;

import dao.CourseDao;
import dao.Sql2oCourseDao;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Course;
import org.sql2o.Sql2o;

import java.util.List;

public class CoursesPageHandler extends RequestHandler {

    CourseDao courseDao;

    public CoursesPageHandler(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new Sql2oCourseDao(this.sql2o);
    }

    @Override
    public void register() {

        this.app.get("/courses", ctx -> {
            ctx.render(
                "/courses.mustache",
                TemplateUtil.model("courseList", this.courseDao.findAll())
            );
        });

        app.get("/courses_json", ctx -> {
            List<Course> courses = courseDao.findAll();
            ctx.json(courses);
            ctx.status(200);
            ctx.contentType("application/json");
        });

    }
}
