package dao;

import exception.DaoException;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Database interface for courses.
 */
public class CourseDao {

    private Sql2o sql2o;

    /**
     * Instantiates a new Course DAO.
     *
     * @param sql2o database connection
     */
    public CourseDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Add a course to the database.
     *
     * @param course course to add
     * @throws DaoException if failed to add course to the database
     */
    public void add(Course course) throws DaoException {
        String sql = "INSERT INTO Courses(name)" +
                     "VALUES(:name);";
        try(Connection conn = sql2o.open()){
            int id = (int) conn.createQuery(sql, true)
                               .addParameter("name", course.getName())
                               .bind(course)
                               .executeUpdate()
                               .getKey();
            course.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add the course", ex);
        }
    }

    /**
     * Remove a course from the database.
     *
     * @param course course to remove
     * @throws DaoException if failed to remove course from database
     */
    public void remove(Course course) throws DaoException {
        NoteDao noteDao = new NoteDao(this.sql2o);
        List<Note> notes = noteDao.findNoteWithCourseId(course.getId());
        for (Note note : notes) {
            noteDao.remove(note);
        }

        String sql = "DELETE FROM Courses WHERE id = :id CASCADE;";
        try(Connection conn = sql2o.open()) {
            System.out.println(course);
            conn.createQuery(sql)
                .addParameter("id", course.getId())
                .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete course", ex);
        }
    }

    /**
     * List all courses.
     *
     * @return list of all courses in the database
     */
    public List<Course> findAll() {
        String sql = "SELECT * FROM Courses;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).executeAndFetch(Course.class);
        }
    }

    /**
     * Find specified course.
     *
     * @param courseId ID of course to fetch
     * @return specified course
     */
    public Course findCourse(int courseId) {
        String sql = "SELECT * FROM Courses WHERE id = :courseId;";
        try(Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                       .addParameter("courseId", courseId)
                       .executeAndFetch(Course.class)
                       .iterator().next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public List<Course> searchCoursesWithName(String searchKey) {
        String sql = "SELECT * FROM Courses WHERE LOWER(name) LIKE LOWER(:courseName);";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).addParameter("courseName", "%" + searchKey + "%").executeAndFetch(Course.class);
        }
    }

}
