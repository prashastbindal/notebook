package daotest;

import dao.CourseDao;
import dao.DBBuilder;
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
        DBBuilder.createTestDatabase("notedaotestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("notedaotestdb");
        DBBuilder.createTables(sql2o, true);

        this.courseDao = new CourseDao(sql2o);
        this.noteDao = new NoteDao(sql2o);

        Course c1 = new Course("CourseName1");
        courseDao.add(c1);
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("notedaotestdb");
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