package dao;

import exception.DaoException;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;
import java.util.NoSuchElementException;

public class CourseDao {

    private Sql2o sql2o;

    public CourseDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

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

    public void remove(Course course) throws DaoException {
        String sql = "DELETE FROM Courses WHERE id = :id;";
        try(Connection conn = sql2o.open()) {
            System.out.println(course);
            conn.createQuery(sql)
                .addParameter("id", course.getId())
                .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete course", ex);
        }
    }

    public List<Course> findAll() {
        String sql = "SELECT * FROM Courses;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql).executeAndFetch(Course.class);
        }
    }

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
}
