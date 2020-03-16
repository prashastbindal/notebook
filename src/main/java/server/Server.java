package server;

import com.google.gson.Gson;
import controller.*;
import dao.*;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JavalinJson;
import org.sql2o.Sql2o;

import java.io.File;

/**
 * NoteBook Server.
 */
public class Server {

    /**
     * Main method for the server.
     *
     * @param args none
     */
    public static void main(String[] args) {

        // open and setup connection with database
        Sql2o sql2o = DBBuilder.getDatabaseConnection();
        DBBuilder.createTables(sql2o, false);
        DBBuilder.createTestData(sql2o, true);

        // start server
        Javalin app = startServer();

        // set the home page
        app.get("/", ctx -> ctx.redirect("/courses"));

        // setup all request handlers
        Controller coursesPageHandler = new MainController(app, sql2o);
        Controller coursePageHandler  = new CourseController(app, sql2o);
        Controller notePageHandler    = new NoteController(app, sql2o);
        Controller adminPageHandler   = new AdminController(app, sql2o);
    }

    /**
     * Setup and start the Javalin server.
     */
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

    /**
     * Determine the port to run the server on.
     *
     * @return port to run on
     */
    private static int getAssignedPort() {
        String herokuPort = System.getenv("PORT");
        if (herokuPort != null) {
            return Integer.parseInt(herokuPort);
        }
        return 7000;
    }

}