package daotest;

import dao.CourseDao;
import dao.DBBuilder;
import dao.SubscriptionDao;
import model.Course;
import model.Subscription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

public class SubscriptionDaoTest {
    private CourseDao courseDao;
    private SubscriptionDao subscriptionDao;
    private Course c1;

    @Before
    public void setUp() throws Exception {
        DBBuilder.createTestDatabase("subscriptiondaoaotestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("subscriptiondaoaotestdb");
        DBBuilder.createTables(sql2o, true);

        this.courseDao = new CourseDao(sql2o);
        this.subscriptionDao = new SubscriptionDao(sql2o);

        c1 = new Course("CourseName1");
        courseDao.add(c1);
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("subscriptiondaoaotestdb");
    }

    @Test
    public void addSubscriptionWorks() {
        Subscription s1 = new Subscription("test@email.com", "un1", c1.getId());
        subscriptionDao.addSubscription(s1);
        Subscription s2 = subscriptionDao.findSubscriptionWithId(s1.getId());
        assertNotNull(s2);
    }

    @Test
    public void remmoveSubscriptionWorks() {
        Subscription s1 = new Subscription("test@email.com", "un1", c1.getId());
        subscriptionDao.addSubscription(s1);
        subscriptionDao.remove(s1);
        Subscription s2 = subscriptionDao.findSubscriptionWithId(s1.getId());
        assertNull(s2);
    }

    @Test
    public void findSubscriptionWithCourseIdWorks() {
        Subscription s1 = new Subscription("test@email.com", "un1", c1.getId());
        subscriptionDao.addSubscription(s1);
        List<Subscription> subs = subscriptionDao.findSubscriptionWithCourseId(c1.getId());
        assertNotEquals(subs.size(), 0);
    }
}
