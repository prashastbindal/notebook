package daotest;

import dao.CourseDao;
import dao.DBBuilder;
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
        DBBuilder.createTestDatabase("coursedaotestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("coursedaotestdb");
        DBBuilder.createTables(sql2o, true);

        this.courseDao = new CourseDao(sql2o);
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("coursedaotestdb");
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