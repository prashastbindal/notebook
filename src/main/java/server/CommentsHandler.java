package server;

import dao.CommentDao;
import dao.Sql2oCommentDao;
import io.javalin.Javalin;
import model.Comment;
import org.sql2o.Sql2o;

public class CommentsHandler extends RequestHandler {

    CommentDao commentDao;

    CommentsHandler(Javalin app, Sql2o sql2o) {
        super(app, sql2o);
    }

    @Override
    void init() {
        this.commentDao = new Sql2oCommentDao(sql2o);
    }

    @Override
    void register() {

        app.post("/courses/:id/notes/:noteId/addComment", ctx -> {
            String courseId = ctx.pathParam("id");
            String noteId = ctx.pathParam("noteId");
            try {
                int nId = Integer.parseInt(noteId);
                Comment comment = new Comment(
                    nId,
                    ctx.formParam("text"),
                    ctx.formParam("creator")
                );
                commentDao.add(comment);
                ctx.redirect("/courses/".concat(courseId).concat("/notes/").concat(noteId).concat("/"));
            } catch (NumberFormatException e) {
                ctx.json("Error 404 not found");
            }
        });

    }
}
