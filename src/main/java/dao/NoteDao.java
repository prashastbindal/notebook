package dao;

import exception.DaoException;
import fileserver.FileServer;
import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Database interface for notes.
 */
public class NoteDao {

    private Sql2o sql2o;
    private FileServer fileServer;

    /**
     * Instantiates a new Note DAO.
     *
     * @param sql2o database connection
     */
    public NoteDao(Sql2o sql2o) {
        this.sql2o = sql2o;
        this.fileServer = FileServer.getFileServer();
    }

    /**
     * Add a note to the database.
     *
     * @param note note to add
     * @throws DaoException if failed to add note to the database
     */
    public void add(Note note) throws DaoException {
        String sql = "INSERT INTO Notes(courseId, title, creator, filetype, date, upvotes)" +
                     "VALUES(:courseId, :title, :creator, :filetype, :date, :upvotes);";
        try(Connection conn = sql2o.open()){
            int id = (int) conn.createQuery(sql, true)
                               .bind(Note.class)
                               .bind(note)
                               .executeUpdate()
                               .getKey();
            note.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add the note", ex);
        }
    }

    /**
     * Remove a note from the database.
     *
     * @param note note to remove
     * @throws DaoException if failed to remove note from the database
     */
    public void remove(Note note) throws DaoException {
        CommentDao commentDao = new CommentDao(this.sql2o);
        List<Comment> comments = commentDao.findCommentWithNoteId(note.getId());
        for (Comment comment : comments) {
            commentDao.remove(comment);
        }

        this.fileServer.remove(note);

        String sql = "DELETE FROM Notes WHERE id = :id;";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                .addParameter("id", note.getId())
                .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete note", ex);
        }
    }

    /**
     * List all notes associated with a specified course.
     *
     * @param courseId ID of the course
     * @return list of all notes for the course
     */
    public List<Note> findNoteWithCourseId(int courseId) {
        String sql = "SELECT * FROM Notes WHERE courseId = :courseId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                       .addParameter("courseId", courseId)
                       .executeAndFetch(Note.class);
        }
    }

    /**
     * Find specified note.
     *
     * @param noteId ID of the note to fetch
     * @return the note
     */
    public Note findNote(int noteId) {
        String sql = "SELECT * FROM Notes WHERE id = :noteId;";
        try(Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                       .addParameter("noteId", noteId)
                       .executeAndFetch(Note.class)
                       .iterator().next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Updates the Note's upvotes.
     *
     * @param note to update
     */
    public void upvote(Note note) {
        String sql = "update Notes set upvotes = :upvotes where id = :noteId;";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("upvotes", note.getUpvotes())
                    .addParameter("noteId", note.getId())
                    .executeUpdate();
        } catch (Exception e) {
            throw new DaoException("Unable to update upvotes", e);
        }
    }

    /**
     * List all notes.
     *
     * @return list of all notes in the database
     */
    public List<Note> findAll() {
        String sql = "SELECT * FROM Notes;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).executeAndFetch(Note.class);
        }
    }

    public List<Note> searchNotesWithName(String searchKey, int courseId) {
        String sql = "SELECT * FROM Notes WHERE title LIKE :noteName and courseid = :courseId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).addParameter("noteName", "%" + searchKey + "%")
                    .addParameter("courseId", courseId).executeAndFetch(Note.class);
        }
    }

}
