package daotest;

import dao.CourseDao;
import dao.NoteDao;
import model.Course;
import model.Note;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class NoteDaoTest {

    CourseDao courseDao;
    NoteDao noteDao;

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
        this.noteDao = new NoteDao(sql2o);

        Course c1 = new Course("CourseName1");
        courseDao.add(c1);
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
    public void addNoteChangesId() {
        Note n1 = new Note(1, "NoteTitle1", "User1", "none");
        assertEquals(0, n1.getId());
        noteDao.add(n1);
        assertNotEquals(0, n1.getId());
    }

    @Test
    public void readNoteWorks() {
        Note n1 = new Note(1, "NoteTitle1", "User1", "none");
        noteDao.add(n1);
        Note n2 = noteDao.findNote(n1.getId());
        assertEquals(n1, n2);
    }

}