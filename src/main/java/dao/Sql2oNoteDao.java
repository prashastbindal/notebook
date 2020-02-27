package dao;

import exception.DaoException;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import java.util.List;

public class Sql2oNoteDao implements NoteDao {

    private Sql2o sql2o;

    public Sql2oNoteDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Note note) throws DaoException {
        String sql = "INSERT INTO Notes(courseId, title, creator)" +
                "VALUES(:courseId, :title, :creator);";
        try(Connection conn = sql2o.open()){
            int id = (int) conn.createQuery(sql, true)
                    .bind(Note.class)
                    .bind(note)
                    .executeUpdate()
                    .getKey();
            note.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add the course", ex);
        }
    }

    @Override
    public void remove(Note note) throws DaoException {
        String sql = "DELETE FROM Notes WHERE id = :id;";
        try(Connection conn = sql2o.open()) {
            System.out.println(note);
            conn.createQuery(sql)
                    .addParameter("id", note.getId())
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete course", ex);
        }
    }


    @Override
    public List<Note> findNoteWithCourseId(int courseId) {
        String sql = "SELECT * FROM Notes WHERE courseId = :courseId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).addParameter("courseId", courseId).executeAndFetch(Note.class);
        }
    }

    @Override
    public Note findNote(int noteId) {
        String sql = "SELECT * FROM Notes WHERE id = :noteId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("noteId", noteId)
                    .executeAndFetch(Note.class)
                    .iterator().next();
        }
    }
}
