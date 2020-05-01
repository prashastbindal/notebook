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
     * Instantiates a new Subscription DAO.
     *
     * @param sql2o database connection
     */
    public SubscriptionDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    /**
     * Add a subscription to the database.
     *
     * @param note subscription to add
     * @throws DaoException if failed to add note to the database
     */
    public void addSubscription(Subscription subscription) throws DaoException{
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

    /**
     * Find subscription associated with the provided attributes object
     *
     * @param subscription object containing attributes of the subscription to be found in database
     * @return retrieved subscription from database
     */
    public Subscription findSubscriptionWithCourseId(Subscription subscription) {
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

    /**
     * Find subscription with its id
     *
     * @param subscriptionId: id of subscription
     * @return retrieved subscription from database
     */
    public Subscription findSubscriptionWithId(int subscriptionId) {
        String sql = "SELECT * FROM Subscriptions WHERE id=:subscriptionId;";
        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("subscriptionId", subscriptionId)
                    .executeAndFetch(Subscription.class)
                    .iterator().next();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    /**
     * Remove subscription associated with the provided attributes object
     *
     * @param subscription : object containing attributes of the subscription to be removed from database
     * @return void
     */
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

    /**
     * Find subscriptions associated with the courseId
     *
     * @param courseId : courseid for which we want to retrieve subscriptions
     * @return subscriptions list from database
     */
    public List<Subscription> findSubscriptionWithCourseId(int courseId) {
        String sql = "SELECT * FROM Subscriptions WHERE courseId = :courseId;";
        try(Connection conn = sql2o.open()){
            return conn.createQuery(sql)
                    .addParameter("courseId", courseId)
                    .executeAndFetch(Subscription.class);
        }
    }
}