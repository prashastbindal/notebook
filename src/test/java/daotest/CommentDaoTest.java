package daotest;

import dao.CommentDao;
import dao.CourseDao;
import dao.DBBuilder;
import dao.NoteDao;
import model.Comment;
import model.Course;
import model.Note;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

public class CommentDaoTest {

    CourseDao courseDao;
    NoteDao noteDao;
    CommentDao commentDao;

    @Before
    public void setUp() throws Exception {

        DBBuilder.createTestDatabase("commentdaotestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("commentdaotestdb");
        DBBuilder.createTables(sql2o, true);

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
        DBBuilder.deleteTestDatabase("commentdaotestdb");
    }

    @Test
    public void addCommentChangesId() {
        Comment c1 = new Comment(0, 1, "This is some comment.", "User1");
        assertEquals(0, c1.getId());
        commentDao.add(c1);
        assertNotEquals(0, c1.getId());
    }

    @Test
    public void readCommentWorks() {
        Comment c1 = new Comment(0, 1, "This is some comment.", "User1");
        commentDao.add(c1);
        Comment c2 = commentDao.findComment(c1.getId());
        assertEquals(c1, c2);
    }

    @Test
    public void findCommentbyNoteIdWorks() {
        Comment c1 = new Comment(0, 1, "This is some comment.", "User1");
        commentDao.add(c1);
        Comment c2 = new Comment(0, 1, "This is a different comment.", "User2");
        commentDao.add(c2);
        List<Comment> l1 = commentDao.findCommentWithNoteId(c1.getNoteId());
        assertEquals(2, l1.size());
    }

    @Test
    public void removeCommentWorks() {
        Comment c1 = new Comment(0, 1, "This is some comment.", "User1");
        commentDao.add(c1);
        commentDao.remove(c1);
        List<Comment> l1 = commentDao.findCommentWithNoteId(c1.getNoteId());
        assertTrue(l1.isEmpty());
    }

}