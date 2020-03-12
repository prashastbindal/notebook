package dao;

import exception.DaoException;
import model.Comment;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;

/**
 * Database interface for comments on notes.
 */
public class CommentDao {

    private Sql2o sql2o;

    /**
     * Instantiates a new Comment DAO.
     *
     * @param sql2o database connection
     */
    public CommentDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Add a comment to the database.
     *
     * @param comment comment to add
     * @throws DaoException if failed to add comment to database
     */
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

    /**
     * Remove a comment from the database.
     *
     * @param comment comment to remove
     * @throws DaoException if failed to remove comment from database
     */
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

    /**
     * Find all comments associated with the specified note.
     *
     * @param noteId ID of the note to fetch comments from
     * @return list of comments
     */
    public List<Comment> findCommentWithNoteId(int noteId) {
        String sql = "SELECT * FROM Comments WHERE noteId = :noteId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                       .addParameter("noteId", noteId)
                       .executeAndFetch(Comment.class);
        }
    }

    /**
     * Find the specified comment in the database.
     *
     * @param commentId ID of the comment to fetch
     * @return specified comment
     */
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
