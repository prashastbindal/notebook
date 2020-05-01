package model;

public class Subscription {
    private String userEmail;
    private String userName;
    private int courseId;
    private int id;

    public Subscription(String userEmail, String userName, int courseId){
        this.userEmail = userEmail;
        this.userName = userName;
        this.courseId = courseId;
    }

    /**
     * Gets the courseId for the subscription
     *
     * @return courseId of the subscription
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Gets the id for the subscription
     *
     * @return id of the subscription
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user email for the subscription
     *
     * @return userEmail of the subscription
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Gets the user name for the subscription
     *
     * @return userName of the subscription
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the course id for the subscription
     *
     * @param courseId of the subscription
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Sets the id for the subscription
     *
     * @param id of the subscription
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the email for the subscription
     *
     * @param userEmail of the subscription
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Sets the user name for the subscription
     *
     * @param userName of the subscription
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
