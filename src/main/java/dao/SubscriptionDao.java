package dao;

import exception.DaoException;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import model.Subscription;

import java.util.List;
import java.util.NoSuchElementException;

public class SubscriptionDao {

    private Sql2o sql2o;

    /**
     * Instantiates a new Course DAO.
     *
     * @param sql2o database connection
     */
    public SubscriptionDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void addSubscription(Subscription subscription) {
//        String sql = "WITH ins AS (" +
//                "  INSERT INTO Users" +
//                "    (name, email, courseId)" +
//                "  VALUES" +
//                "    (:name,  :email, :courseId)" +
//                "  RETURNING email)," +
//                "INSERT INTO Subscriptions" +
//                "  (courseId, email)" +
//                "SELECT id, 'test data'" +
//                "FROM ins" +
//                "ON CONFLICT DO NOTHING'";

        String sql = "INSERT INTO Subscriptions" +
                "   (userName, userEmail, courseId)" +
                " VALUES" +
                "   (:userName,  :userEmail, :courseId)";

        try (Connection conn = sql2o.open()) {
            int id = (int) conn.createQuery(sql, true)
                    .bind(subscription)
                    .executeUpdate()
                    .getKey();
            subscription.setId(id);
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to add the subscription", ex);
        }
    }

    public Subscription findSubscription(Subscription subscription) {
        String sql = "SELECT * FROM Subscriptions WHERE userName = :userName AND userEmail = :userEmail AND courseId = :courseId;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .bind(subscription)
                    .executeAndFetch(Subscription.class)
                    .iterator().next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    public void remove(Subscription subscription) throws DaoException {
        String sql = "DELETE FROM Subscriptions WHERE id = :id;";
        try(Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .bind(subscription)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DaoException("Unable to delete subscription", ex);
        }
    }


    public List<Subscription> findSubscriptoinsWithCourseId(int courseId) {
        String sql = "SELECT * FROM Subscriptions WHERE courseId = :courseId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("courseId", courseId)
                    .executeAndFetch(Subscription.class);
        }
    }
}