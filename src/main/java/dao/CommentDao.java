package dao;

import exception.DaoException;
import model.Comment;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;

public class CommentDao {

    private Sql2o sql2o;

    public CommentDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void add(Comment comment) throws DaoException {
        String sql = "INSERT INTO Comments(noteId, text, creator)" +
                     "VALUES(:noteId, :text, :creator);";
        try(Connection conn = sql2o.open()){
            int id = (int) conn.createQuery(sql, true)
                               .bind(Comment.class)
                               .bind(comment)
                               .executeUpdate()
                               .getKey();
            comment.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add the comment", ex);
        }
    }

    public void remove(Comment comment) throws DaoException {
        String sql = "DELETE FROM Comments WHERE id = :id;";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                .addParameter("id", comment.getId())
                .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete comment", ex);
        }
    }

    public List<Comment> findCommentWithNoteId(int noteId) {
        String sql = "SELECT * FROM Comments WHERE noteId = :noteId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                       .addParameter("noteId", noteId)
                       .executeAndFetch(Comment.class);
        }
    }

    public Comment findComment(int commentId) {
        String sql = "SELECT * FROM Comments WHERE id = :commentId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                       .addParameter("commentId", commentId)
                       .executeAndFetch(Comment.class)
                       .iterator().next();
        }
    }
}
