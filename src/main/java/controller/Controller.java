package controller;

import io.javalin.Javalin;
import org.sql2o.Sql2o;

public abstract class Controller {

    Javalin app;
    Sql2o sql2o;

    Controller(Javalin app, Sql2o sql2o) {
        this.app = app;
        this.sql2o = sql2o;

        this.init();
        this.register();
    }

    abstract void init();
    abstract void register();
}
