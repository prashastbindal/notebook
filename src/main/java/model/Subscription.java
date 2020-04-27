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

    public int getCourseId() {
        return courseId;
    }

    public int getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
