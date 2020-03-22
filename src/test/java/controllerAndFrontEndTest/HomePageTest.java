package controllerAndFrontEndTest;

import com.google.gson.Gson;
import controller.Controller;
import controller.CourseController;
import controller.CourseListController;
import controller.NoteController;
import dao.CommentDao;
import dao.CourseDao;
import dao.DBBuilder;
import dao.NoteDao;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.json.JavalinJson;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.sql2o.Sql2o;
import java.io.File;
import java.util.List;

import static org.junit.Assert.*;


public class HomePageTest {
    private NoteDao noteDao;
    private CourseDao courseDao;
    private CommentDao commentDao;
    private WebDriver driver;

    @Before
    public void setup() throws Exception {

        DBBuilder.createTestDatabase("homepagetestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("homepagetestdb");
        DBBuilder.createTables(sql2o, true);
        DBBuilder.createTestData(sql2o, true);

        this.courseDao = new CourseDao(sql2o);
        this.noteDao = new NoteDao(sql2o);
        this.commentDao = new CommentDao(sql2o);
        Gson gson = new Gson();
        JavalinJson.setToJsonMapper(gson::toJson);
        JavalinJson.setFromJsonMapper(gson::fromJson);

        final int PORT = Integer.parseInt(System.getenv("PORT"));

        if (! new File("static/").exists()) {
            new File("static/").mkdirs();
        }

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("static/", Location.EXTERNAL);
        }).start(PORT);

        app.get("/", ctx -> ctx.redirect("/courses"));

        Controller coursesPageHandler = new CourseListController(app, sql2o);
        Controller coursePageHandler  = new CourseController(app, sql2o);
        Controller notePageHandler    = new NoteController(app, sql2o);
        System.setProperty("webdriver.chrome.driver","src\\test\\java\\controllerAndFrontEndTest\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("homepagetestdb");
    }

    @Test
    public void correctPage() {
        driver.get("http://localhost:7000/courses");
        assertEquals("NoteBook", driver.getTitle());
    }

    @Test
    public void coursesAreListed() {
        driver.get("http://localhost:7000/courses");
        WebElement list = driver.findElement(By.tagName("ul"));
        List<WebElement> links = driver.findElements(By.tagName("li"));
        WebElement exampleCourseOne = links.get(0);
        assertEquals("Example Course 1", exampleCourseOne.getText());
    }

    @Test
    public void courseLinkWorks() {
        driver.get("http://localhost:7000/courses");
        List<WebElement> links = driver.findElements(By.tagName("a"));
        WebElement exampleCourseOneLink = links.get(0);
        exampleCourseOneLink.click();
        assertEquals("http://localhost:7000/courses/1/notes/", driver.getCurrentUrl());
    }

}
