package server;

import com.google.gson.Gson;
import controller.*;
import dao.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JavalinJson;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.io.File;

public class Server {
    public static void main(String[] args) {

        // hack to make jar build properly
        /*
        try {
          Class.forName("org.postgresql.JDBC");
        } catch (ClassNotFoundException e) {}
         */

        // open connection with database
        Sql2o sql2o = connectSql2o();

        // setup database tables
        createCoursesTable(sql2o);
        createNotesTable(sql2o);
        createCommentTable(sql2o);

        // create test data
        createTestData(sql2o);

        // start server
        Javalin app = startServer();

        // set the home page
        app.get("/", ctx -> ctx.redirect("/courses"));

        // setup all request handlers
        Controller coursesPageHandler = new MainController(app, sql2o);
        Controller coursePageHandler  = new CourseController(app, sql2o);
        Controller notePageHandler    = new NoteController(app, sql2o);
    }

    private static Javalin startServer() {

        Gson gson = new Gson();
        JavalinJson.setToJsonMapper(gson::toJson);
        JavalinJson.setFromJsonMapper(gson::fromJson);

        final int PORT = getAssignedPort();

        if (! new File("static/").exists()) {
            new File("static/").mkdirs();
        }

        return Javalin.create(config -> {
            config.addStaticFiles("static/", Location.EXTERNAL);
        }).start(PORT);
    }

    private static int getAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

    private static Sql2o connectSql2o() {
        String dbURI = System.getenv("JDBC_DATABASE_URL");
        if (dbURI == null) {
            throw new IllegalArgumentException();
        }
        return new Sql2o(dbURI, "", "");
    }

    private static void createCoursesTable(Sql2o sql2o) {
        String sql = "CREATE TABLE IF NOT EXISTS Courses(" +
                "id SERIAL PRIMARY KEY," +
                "name VARCHAR(30) NOT NULL" +
                ");";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql).executeUpdate();
        }
    }

    private static void createNotesTable(Sql2o sql2o) {
        String sql = "CREATE TABLE IF NOT EXISTS Notes(" +
                "id SERIAL PRIMARY KEY," +
                "courseId INTEGER NOT NULL," +
                "title VARCHAR(30) NOT NULL," +
                "creator VARCHAR(30)," +
                "filetype VARCHAR(30)," +
                "FOREIGN KEY (courseId) REFERENCES Courses (id));";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql).executeUpdate();
        }
    }
    private static void createCommentTable(Sql2o sql2o) {
        String sql = "CREATE TABLE IF NOT EXISTS Comments(" +
                "id SERIAL PRIMARY KEY," +
                "noteId INTEGER NOT NULL," +
                "text VARCHAR(1000) NOT NULL," +
                "creator VARCHAR(30)," +
                "FOREIGN KEY (noteId) REFERENCES Notes (id)" +
                ");";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql).executeUpdate();
        }
    }

    private static void createTestData(Sql2o sql2o) {

        CourseDao courseDao = new Sql2oCourseDao(sql2o);
        NoteDao noteDao = new Sql2oNoteDao(sql2o);
        CommentDao commentDao = new Sql2oCommentDao(sql2o);

        if (courseDao.findAll().isEmpty()) {
            Javalin.log.info("Creating test data...");

            Course c1 = new Course("Example Course 1");
            Course c2 = new Course("Example Course 2");
            courseDao.add(c1);
            courseDao.add(c2);

            Note n1 = new Note(c1.getId(), "Note1", "student1", "none");
            Note n2 = new Note(c1.getId(), "Note2", "student2", "none");
            Note n3 = new Note(c2.getId(), "Note3", "student1", "none");
            noteDao.add(n1);
            noteDao.add(n2);
            noteDao.add(n3);

            Comment cmt1 = new Comment(n1.getId(), "this is a comment", "student 1");
            Comment cmt2 = new Comment(n1.getId(), "this is also one!", "student 2");
            Comment cmt3 = new Comment(n2.getId(), "test data comment. Hello!", "Matt");
            commentDao.add(cmt1);
            commentDao.add(cmt2);
            commentDao.add(cmt3);
        }
    }

}
