package daotest;

import dao.CommentDao;
import dao.CourseDao;
import dao.NoteDao;
import model.Comment;
import model.Course;
import model.Note;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class CommentDaoTest {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;

    @Before
    public void setUp() throws Exception {
        try {
            Class.forName("org.postgresql.JDBC");
        } catch (ClassNotFoundException e) {}

        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("CREATE DATABASE commentdaotestdb;").executeUpdate();
        conn.close();

        dbURI = "jdbc:postgresql://localhost:5432/commentdaotestdb";
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
        this.commentDao = new CommentDao(sql2o);

        Course c1 = new Course("CourseName1");
        Course c2 = new Course("CourseName2");
        this.courseDao.add(c1);
        this.courseDao.add(c2);

        Note n1 = new Note(c1.getId(), "Note 1", "User1", "none");
        Note n2 = new Note(c2.getId(), "Note 2", "User2", "html");
        this.noteDao.add(n1);
        this.noteDao.add(n2);
    }

    @After
    public void tearDown() throws Exception {
        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("DROP DATABASE commentdaotestdb;").executeUpdate();
        conn.close();
    }

    @Test
    public void readCommentWorks() {
        Comment c1 = new Comment(1, "This is some comment.", "User1");
        commentDao.add(c1);
        Comment c2 = commentDao.findComment(c1.getId());
        assertEquals(c1, c2);
    }

}