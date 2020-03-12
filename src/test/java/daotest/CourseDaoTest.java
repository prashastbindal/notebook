package daotest;

import dao.CourseDao;
import model.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class CourseDaoTest {

    CourseDao courseDao;

    @Before
    public void setUp() throws Exception {
        try {
            Class.forName("org.postgresql.JDBC");
        } catch (ClassNotFoundException e) {}

        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("CREATE DATABASE coursedaotestdb;").executeUpdate();
        conn.close();

        dbURI = "jdbc:postgresql://localhost:5432/coursedaotestdb";
        sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");
        conn = sql2o.open();

        String sqlCreateCoursesTable =
                "CREATE TABLE IF NOT EXISTS Courses(" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(30) NOT NULL" +
                ");";
        String sqlCreateNotesTable =
                "CREATE TABLE IF NOT EXISTS Notes(" +
                    "id SERIAL PRIMARY KEY," +
                    "courseId INTEGER NOT NULL," +
                    "title VARCHAR(30) NOT NULL," +
                    "creator VARCHAR(30)," +
                    "filetype VARCHAR(30)," +
                    "FOREIGN KEY (courseId) REFERENCES Courses (id)" +
                ");";
        String sqlCreateCommentsTable =
                "CREATE TABLE IF NOT EXISTS Comments(" +
                    "id SERIAL PRIMARY KEY," +
                    "noteId INTEGER NOT NULL," +
                    "text VARCHAR(1000) NOT NULL," +
                    "creator VARCHAR(30)," +
                    "FOREIGN KEY (noteId) REFERENCES Notes (id)" +
                ");";

        conn.createQuery(sqlCreateCoursesTable).executeUpdate();
        conn.createQuery(sqlCreateNotesTable).executeUpdate();
        conn.createQuery(sqlCreateCommentsTable).executeUpdate();
        conn.close();

        this.courseDao = new CourseDao(sql2o);
    }

    @After
    public void tearDown() throws Exception {
        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("DROP DATABASE coursedaotestdb;").executeUpdate();
        conn.close();
    }

    @Test
    public void addCourseChangesId() {
        Course c1 = new Course("CourseName1");
        assertEquals(0, c1.getId());
        courseDao.add(c1);
        assertNotEquals(0, c1.getId());
    }

    @Test
    public void readCourseWorks() {
        Course c1 = new Course("CourseName1");
        courseDao.add(c1);
        Course c2 = courseDao.findCourse(c1.getId());
        assertEquals(c1, c2);
    }

}