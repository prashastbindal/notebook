package controller;

import dao.CourseDao;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.rendering.template.TemplateUtil;
import model.Course;
import org.sql2o.Sql2o;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Controller for the list of courses.
 */
public class CourseListController extends Controller {

    CourseDao courseDao;

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    public CourseListController(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.courseDao = new CourseDao(this.sql2o);
    }

    @Override
    public void register() {

        app.routes(() -> {
            path("/courses", () -> {
                get(this::getCourses);
                get("json", this::getCoursesJSON);
                get("search/:key", this::searchCoursesJSON);
            });
        });

    }

    /**
     * Handler for course list page.
     *
     * @param ctx request context
     */
    public void getCourses(Context ctx) {
        ctx.render(
            "/templates/courses.mustache",
            TemplateUtil.model("courseList", this.courseDao.findAll())
        );
    }

    /**
     * Handler for course list JSON requests.
     *
     * @param ctx request context
     */
    public void getCoursesJSON(Context ctx) {
        List<Course> courses = this.courseDao.findAll();
        ctx.json(courses);
        ctx.status(200);
        ctx.contentType("application/json");
    }

    public void searchCoursesJSON(Context ctx){
        String searchKey = ctx.pathParam("key");
        List<Course> courses = courseDao.searchCoursesWithName(searchKey);
        ctx.json(courses);
        ctx.status(200);
        ctx.contentType("application/json");
    }

}
