package api;

import com.google.gson.Gson;
import dao.CourseDao;
import dao.NoteDao;
import dao.Sql2oCourseDao;
import dao.Sql2oNoteDao;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import io.javalin.plugin.rendering.template.JavalinMustache;
import io.javalin.plugin.rendering.template.TemplateUtil;

import java.util.List;

public class ApiServer {
  public static void main(String[] args) {
    // create a database
    Sql2o sql2o = createSql2o();
    // add Courses and Notes tables to database
    createCoursesTable(sql2o);
    createNotesTable(sql2o);
    // connect the database to CourseDao and NoteDao
    CourseDao courseDao = createCourseDao(sql2o);
    NoteDao noteDao = createNoteDao(sql2o);
    // Run the server
    Javalin app = createJavalinServer();

    Course c1 = new Course("Example Course 1");
    Course c2 = new Course("Example Course 1");
    courseDao.add(c1);
    courseDao.add(c2);

    // HTTP Get for the fist page
    app.get("/", ctx -> ctx.result("Welcome to NoteBook APP!"));

    // Get the HTTP Get request and respond with courses in the database
    app.get("/courses_json", ctx -> {
      List<Course> courses = courseDao.findAll();
      ctx.json(courses);
      ctx.status(200);
      ctx.contentType("application/json");
    });

    app.get("/courses", ctx -> {
      ctx.render("/courses.mustache", TemplateUtil.model("courseList", courseDao.findAll()));
    });

    app.post("/courses", ctx -> {
      // Typically, the user provides the course as a JSON object.
      // Maps a JSON body to a Java class using JavalinJson:
      Course course = ctx.bodyAsClass(Course.class);
      // Next, add the course to our database using our DAO:
      courseDao.add(course);
      // Conventionally, return the added course to client:
      ctx.json(course);
      ctx.contentType("application/json");
      // Also, return 201 status code to say operation succeeded.
      ctx.status(201);
    });

    app.delete("/courses", ctx -> {
      Course course = ctx.bodyAsClass(Course.class);
      System.out.println(course.getId());
      courseDao.remove(course);
      ctx.contentType("application/json");
      ctx.status(201);
    });

    app.get("/courses/:id/notes", ctx -> {
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

    app.post("/courses/:id/notes", ctx -> {
      Note note = ctx.bodyAsClass(Note.class);
      noteDao.add(note);
      ctx.json(note);
      ctx.contentType("application/json");
      ctx.status(201);
    });

    app.delete("/courses/:id/notes", ctx -> {
      Note note = ctx.bodyAsClass(Note.class);
      System.out.println(note.getId());
      noteDao.remove(note);
      ctx.contentType("application/json");
      ctx.status(201);
    });

  }

  private static Javalin createJavalinServer() {
    Gson gson = new Gson();
    JavalinJson.setToJsonMapper(gson::toJson);
    JavalinJson.setFromJsonMapper(gson::fromJson);
    final int PORT = 7000;
//    return Javalin.create(config -> {
//      config.addStaticFiles("resources");
//    }).start(PORT);
    return Javalin.create().start(PORT);
  }

  private static CourseDao createCourseDao(Sql2o sql2o) {
    return new Sql2oCourseDao(sql2o);
  }

  private static NoteDao createNoteDao(Sql2o sql2o) {
    return new Sql2oNoteDao(sql2o);
  }

  private static void createCoursesTable(Sql2o sql2o) {
    String sql = "CREATE TABLE IF NOT EXISTS Courses(" +
                    "id INTEGER PRIMARY KEY," +
                    "name VARCHAR(30) NOT NULL" +
                  ");";
    try(Connection conn = sql2o.open()) {
      conn.createQuery(sql).executeUpdate();
    }
  }

  private static void createNotesTable(Sql2o sql2o) {
    String sql = "CREATE TABLE IF NOT EXISTS Notes(" +
            "id INTEGER PRIMARY KEY," +
            "courseId Integer NOT NULL," +
            "title VARCHAR(30) NOT NULL," +
            "creator VARCHAR(30)," +
            "FOREIGN KEY (courseId) REFERENCES Courses (id));";
    try(Connection conn = sql2o.open()) {
      conn.createQuery(sql).executeUpdate();
    }
  }

  private static Sql2o createSql2o() {
    final String URI = "jdbc:sqlite:./Courses.db";
    final String USERNAME = "";
    final String PASSWORD = "";
    return new Sql2o(URI, USERNAME, PASSWORD);
  }
}
