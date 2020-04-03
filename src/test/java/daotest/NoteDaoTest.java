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

import java.util.List;

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

    @Test
    public void findNoteWithCourseIdWorks() {
        Note n1 = new Note(1, "Note1", "User1", "txt");
        noteDao.add(n1);
        Note n2 = new Note(1, "Note2", "User2", "txt");
        noteDao.add(n2);
        List<Note> l1 = noteDao.findNoteWithCourseId(1);
        assertEquals(2, l1.size());
    }

    @Test
    public void removeNoteWorks() {
        Note n1 = new Note(1, "Note1", "User1", "txt");
        noteDao.add(n1);
        noteDao.remove(n1);
        List<Note> l1 = noteDao.findNoteWithCourseId(n1.getCourseId());
        assertTrue(l1.isEmpty());
    }

    @Test
    public void searchWorks() {
        Note n1 = new Note(1, "Note1", "User1", "txt");
        n1.setFulltext("This is some example text for Note1 created by User1.");
        noteDao.add(n1);

        Note n2 = new Note(1, "Note2", "User1", "txt");
        n2.setFulltext("This is some example text for Note2 created by User1.");
        noteDao.add(n2);

        List<Note> found = noteDao.search("text for Note2", 1);
        assertEquals(1, found.size());
        assertEquals(n2, found.get(0));
    }
}