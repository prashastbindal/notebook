package controller;

import io.javalin.Javalin;
import org.sql2o.Sql2o;

/**
 * Base class for controllers.
 */
public abstract class Controller {

    Javalin app;
    Sql2o sql2o;

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    Controller(Javalin app, Sql2o sql2o) {
        this.app = app;
        this.sql2o = sql2o;

        this.init();
        this.register();
    }

    /**
     * Setup DAO, file server, etc. required for controller.
     */
    abstract void init();

    /**
     * Register the handled endpoints with Javalin.
     */
    abstract void register();
}
