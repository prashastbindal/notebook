package api;

import com.google.gson.Gson;
import dao.CourseDao;
import dao.Sql2oCourseDao;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import model.Course;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

public class ApiServer {
  public static void main(String[] args) {


    // create a database
    Sql2o sql2o = createSql2o();
    // add Courses table to it
    createCoursesTable(sql2o);
    // connect the database to my CourseDao
    CourseDao courseDao = createCourseDao(sql2o);
    // add some sample data to it
    createSampleCourses(courseDao);
    // Run the server
    Javalin app = createJavalinServer();

    // HTTP Get for the fist page
    app.get("/", ctx -> ctx.result("Welcome to CourseReVU APP!"));

    // Get the HTTP Get request and respond with courses in the database
    app.get("/courses", ctx -> {
      List<Course> courses = courseDao.findAll();
      ctx.json(courses);
      ctx.status(200);
      ctx.contentType("application/json");
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

  }

  private static Javalin createJavalinServer() {
    Gson gson = new Gson();
    JavalinJson.setToJsonMapper(gson::toJson);
    JavalinJson.setFromJsonMapper(gson::fromJson);
    final int PORT = 7000;
    return Javalin.create().start(PORT);
  }

  private static void createSampleCourses(CourseDao courseDao) {
    courseDao.add(new Course("OOSE"));
    courseDao.add(new Course("Gateway"));
  }

  private static CourseDao createCourseDao(Sql2o sql2o) {
    return new Sql2oCourseDao(sql2o);
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

  private static Sql2o createSql2o() {
    final String URI = "jdbc:sqlite:./Courses.db";
    final String USERNAME = "";
    final String PASSWORD = "";
    return new Sql2o(URI, USERNAME, PASSWORD);
  }
}
