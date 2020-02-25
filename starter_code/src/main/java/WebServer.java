import dao.CourseDao;
import dao.NoteDao;
import dao.UnirestCourseDao;
import dao.UnirestNoteDao;
import model.Course;
import model.Note;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class WebServer {
    public static void main(String[] args) {

        CourseDao courseDao = new UnirestCourseDao();
        // Preload these courses once.
        //courseDao.add(new Course("OOSE"));
        //courseDao.add(new Course("Data Structures"));
        NoteDao noteDao = new UnirestNoteDao();

        get("/", ((request, response) -> {
            return new ModelAndView(null, "index.hbs");
        }), new HandlebarsTemplateEngine());

        get("/courses", ((request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("courseList", courseDao.findAll());
            return new ModelAndView(model, "courses.hbs");
        }),  new HandlebarsTemplateEngine());

        // For Iteration 1, just preloading 2 courses.
        // Super crude but we can add better functionality with
        // code reuse later
        get("/OOSE-notes", ((request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("noteList", noteDao.findNoteWithCourseId(1));
            return new ModelAndView(model, "OOSE-notes.hbs");
        }),  new HandlebarsTemplateEngine());

        post("/OOSE-notes", ((request, response) -> {
            String title = request.queryParams("title");
            String creator = request.queryParams("creator");
            noteDao.add(new Note(1, title, creator));
            response.redirect("/OOSE-notes");
            return null;
        }), new HandlebarsTemplateEngine());

        get("/DS-notes", ((request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("noteList", noteDao.findNoteWithCourseId(2));
            return new ModelAndView(model, "DS-notes.hbs");
        }),  new HandlebarsTemplateEngine());

        post("/DS-notes", ((request, response) -> {
            String title = request.queryParams("title");
            String creator = request.queryParams("creator");
            noteDao.add(new Note(2, title, creator));
            response.redirect("/DS-notes");
            return null;
        }), new HandlebarsTemplateEngine());
    }
}

