package controller;

import io.javalin.Javalin;
import org.sql2o.Sql2o;

public class StaticPageController extends Controller {

    /**
     * Instantiates a new controller.
     *
     * @param app   Javalin connection
     * @param sql2o database connection
     */
    public StaticPageController(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
    }

    /**
     * Register the handled endpoints with Javalin.
     */
    @Override
    void register() {
        app.get("/", ctx -> ctx.redirect("/home"));

        app.get("/home", ctx -> {
            ctx.render("/templates/home.mustache");
        });

        app.get("/aboutUs", ctx -> {
            ctx.render("/templates/aboutUs.mustache");
        });

        app.get("/contact", ctx -> {
            ctx.render("/templates/contact.mustache");
        });
    }
}