package dao;

import model.Comment;
import model.Course;
import model.Note;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

/**
 * Static class for managing the database.
 */
public final class DBBuilder {

    private DBBuilder() {}

    /**
     * Gets a connection to the database specified by the JDBC_DATABASE_URL
     * environment variable.
     *
     * @return database connection
     */
    public static Sql2o getDatabaseConnection() {
        try {
            Class.forName("org.postgresql.JDBC");
        } catch (ClassNotFoundException ignored) {}

        String dbURI = System.getenv("JDBC_DATABASE_URL");
        if (dbURI == null) {
            throw new IllegalArgumentException();
        }
        return new Sql2o(dbURI, "", "");
    }

    /**
     * Gets a connection to the specified database.
     *
     * @param dbURI    URI of the database
     * @param user     database username
     * @param password database password
     * @return database connection
     */
    public static Sql2o getDatabaseConnection(String dbURI, String user, String password) {
        return new Sql2o(dbURI, user, password);
    }

    /**
     * Gets a connection to a local database used for testing.
     *
     * @param dbName database name
     * @return database connection
     */
    public static Sql2o getTestDatabaseConnection(String dbName) {
        String dbURI = "jdbc:postgresql://localhost:5432/" + dbName;
        return new Sql2o(dbURI, "dbuser", "dbpasswd");
    }

    /**
     * Create a local test database.
     *
     * @param dbName database name
     */
    public static void createTestDatabase(String dbName) {
        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("CREATE DATABASE " + dbName + ";").executeUpdate();
        conn.close();
    }

    /**
     * Delete a local test database.
     *
     * @param dbName database name
     */
    public static void deleteTestDatabase(String dbName) {
        String dbURI = "jdbc:postgresql://localhost:5432/postgres";
        Sql2o sql2o = new Sql2o(dbURI, "dbuser", "dbpasswd");

        Connection conn = sql2o.open();
        conn.createQuery("DROP DATABASE " + dbName + ";").executeUpdate();
        conn.close();
    }

    /**
     * Create the Course, Note, and Comment tables.
     *
     * @param sql2o       database connection
     * @param dropIfExist reset the tables if they exist
     */
    public static void createTables(Sql2o sql2o, Boolean dropIfExist) {
        if (dropIfExist) DBBuilder.dropTablesIfExist(sql2o);

        String sqlCreateCoursesTable =
                "CREATE TABLE IF NOT EXISTS Courses(" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(30) NOT NULL" +
                ");";
        String sqlCreateNotesTable =
                "CREATE TABLE IF NOT EXISTS Notes(" +
                    "id SERIAL PRIMARY KEY," +
                    "courseId INTEGER NOT NULL REFERENCES Courses(id)," +
                    "title VARCHAR(80) NOT NULL," +
                    "creator VARCHAR(80)," +
                    "filetype VARCHAR(30)," +
                    "date VARCHAR(10)," +
                    "upvotes INTEGER," +
                    "fulltext TEXT" +
                ");";
        String sqlCreateCommentsTable =
                "CREATE TABLE IF NOT EXISTS Comments(" +
                    "id SERIAL PRIMARY KEY," +
                    "parentId SERIAL," +
                    "noteId INTEGER NOT NULL REFERENCES Notes(id)," +
                    "text TEXT NOT NULL," +
                    "creator VARCHAR(80)" +
                ");";

        String sqlCreateSubscriptionsTable =
                "CREATE TABLE IF NOT EXISTS Subscriptions(" +
                        "id SERIAL PRIMARY KEY," +
                        "userName VARCHAR(80)," +
                        "courseId INTEGER NOT NULL REFERENCES Courses(id)," +
                        "userEmail  VARCHAR(80)" +
                        ");";

        Connection conn = sql2o.open();
        conn.createQuery(sqlCreateCoursesTable).executeUpdate();
        conn.createQuery(sqlCreateNotesTable).executeUpdate();
        conn.createQuery(sqlCreateCommentsTable).executeUpdate();
        conn.createQuery(sqlCreateSubscriptionsTable).executeUpdate();
        conn.close();
    }

    /**
     * Remove the Course, Note, and Comment tables if they already exist.
     *
     * @param sql2o database connection
     */
    public static void dropTablesIfExist(Sql2o sql2o) {
        String sqlDropCommentsTable = "DROP TABLE IF EXISTS Comments;";
        String sqlDropNotesTable    = "DROP TABLE IF EXISTS Notes;";
        String sqlDropCoursesTable  = "DROP TABLE IF EXISTS Courses CASCADE;";
        String sqlDropSubscriptionsTable  = "DROP TABLE IF EXISTS Subscriptions;";

        Connection conn = sql2o.open();
        conn.createQuery(sqlDropCommentsTable).executeUpdate();
        conn.createQuery(sqlDropNotesTable).executeUpdate();
        conn.createQuery(sqlDropCoursesTable).executeUpdate();
        conn.createQuery(sqlDropSubscriptionsTable).executeUpdate();
        conn.close();
    }

    /**
     * Populate the database with some basic test data.
     *
     * @param sql2o database connection
     */
    public static void createTestData(Sql2o sql2o, Boolean onlyIfEmpty) {

        CourseDao courseDao = new CourseDao(sql2o);
        NoteDao noteDao = new NoteDao(sql2o);
        CommentDao commentDao = new CommentDao(sql2o);

        if (courseDao.findAll().isEmpty() || (!onlyIfEmpty)) {
            Course c1 = new Course("Example Course 1");
            Course c2 = new Course("Example Course 2");
            courseDao.add(c1);
            courseDao.add(c2);

            Note n1 = new Note(c1.getId(), "Note1", "student1", "none");
            Note n2 = new Note(c1.getId(), "Note2", "student2", "none");
            Note n3 = new Note(c2.getId(), "Note3", "student1", "none");
            noteDao.add(n1);
            noteDao.add(n2);
            noteDao.add(n3);

            Comment cmt1 = new Comment(0, n1.getId(), "this is a comment", "student 1");
            Comment cmt2 = new Comment(0, n2.getId(), "this is also one!", "student 2");
            commentDao.add(cmt1);
            commentDao.add(cmt2);
            Comment cmt3 = new Comment(cmt1.getId(), n1.getId(), "test comment reply. Hello!", "Matt");
            commentDao.add(cmt3);
        }
    }

}
