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
        String sql = "INSERT INTO Notes(courseId, title, creator, filetype, date, upvotes, fulltext)" +
                     "VALUES(:courseId, :title, :creator, :filetype, :date, :upvotes, :fulltext);";
        try(Connection conn = sql2o.open()){
            int id = (int) conn.createQuery(sql, true)
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
    public void updateUpvotes(Note note) {
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
     * Updates the Note's fulltext.
     *
     * @param note note to update
     */
    public void updateFulltext(Note note) {
        String sql = "UPDATE Notes SET fulltext = :fulltext where id = :noteId;";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("fulltext", note.getFulltext())
                    .addParameter("noteId", note.getId())
                    .executeUpdate();
        } catch (Exception e) {
            throw new DaoException("Unable to update fulltext.", e);
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

    /**
     * Search for notes by fulltext.
     *
     * @param searchValue search query
     * @param courseId search notes for courseId
     * @return list of matching notes
     */
    public List<Note> searchWithBodyText(String searchValue, int courseId) {
        return searchNotes("fulltext", "%" + searchValue + "%", courseId);
    }

    /**
     * Search for notes by title/name.
     *
     * @param searchValue search query
     * @param courseId search notes for courseId
     * @return list of matching notes
     */
    public List<Note> searchWithName(String searchValue, int courseId) {
        return searchNotes("title", "%" + searchValue + "%", courseId);
    }

    /**
     * Search for notes by creator.
     *
     * @param searchValue search query
     * @param courseId search notes for courseId
     * @return list of matching notes
     */
    public List<Note> searchByCreator(String searchValue, int courseId) {
        return searchNotes("creator", "%" + searchValue + "%", courseId);
    }

    /**
     * Search for notes by date.
     *
     * @param searchValue search query
     * @param courseId search notes for courseId
     * @return list of matching notes
     */
    public List<Note> searchByDate(String searchValue, int courseId) {
        if (searchValue.length() == 8) {
            searchValue = searchValue.substring(0, 2) + "/" + searchValue.substring(2, 4) + "/" + searchValue.substring(4);
        }
        return searchNotes("date", searchValue, courseId);
    }


    /**
     * Search for notes by attribute searchBy with value searchValue.
     *
     * @param searchBy search by attribute
     * @param searchValue search query
     * @param courseId search notes for courseId
     * @return list of matching notes
     */
    private List<Note> searchNotes(String searchBy, String searchValue, int courseId){
        String sql = "SELECT * FROM NOTES WHERE LOWER(" + searchBy +") LIKE LOWER(:searchValue) AND courseid = :courseId;";
        try(Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("searchValue", "%" + searchValue + "%")
                    .addParameter("courseId", courseId)
                    .executeAndFetch(Note.class);
        } catch (NoSuchElementException e) {
            return null;
        }

    }

}
