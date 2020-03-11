package server;

import io.javalin.Javalin;
import org.sql2o.Sql2o;

public abstract class RequestHandler {

    Javalin app;
    Sql2o sql2o;

    RequestHandler(Javalin app, Sql2o sql2o) {
        this.app = app;
        this.sql2o = sql2o;

        this.init();
        this.register();
    }

    abstract void init();
    abstract void register();
}
