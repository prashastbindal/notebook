package controllerAndFrontEndTest;

import com.google.gson.Gson;
import controller.*;
import dao.CommentDao;
import dao.CourseDao;
import dao.DBBuilder;
import dao.NoteDao;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJson;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.sql2o.Sql2o;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.*;
import static org.junit.Assert.*;


public class CoursesListPageTest {
    private NoteDao noteDao;
    private CourseDao courseDao;
    private CommentDao commentDao;

    private static DriverService service;
    private WebDriver driver;
    private Javalin app;

    @BeforeClass
    public static void setupService() throws IOException {
        String chromeDriverPath;
        if (IS_OS_MAC) {
            chromeDriverPath = "src/test/resources/chromedriver/chromedriver_mac";
        } else if (IS_OS_LINUX) {
            chromeDriverPath = "src/test/resources/chromedriver/chromedriver_linux";
        } else if (IS_OS_WINDOWS) {
            chromeDriverPath = "src\\test\\resources\\chromedriver\\chromedriver.exe";
        } else {
            throw new IllegalStateException("Unsupported OS");
        }

        service = new ChromeDriverService.Builder()
                .usingDriverExecutable(new File(chromeDriverPath))
                .build();
        service.start();
    }

    @AfterClass
    public static void destroyService() {
        service.stop();
    }

    @Before
    public void setupDriver() {
        driver = new ChromeDriver((ChromeDriverService) service, new ChromeOptions().setHeadless(true));
    }

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

        final int PORT = 7000;

        app = Javalin.create(config -> {
            config.addStaticFiles("public/");
        }).start(PORT);

        Controller staticPagesHandler = new StaticPageController(app, sql2o);
        Controller coursesPageHandler = new CourseListController(app, sql2o);
        Controller coursePageHandler  = new CourseController(app, sql2o);
        Controller notePageHandler    = new NoteController(app, sql2o);
        Controller adminPageHandler   = new AdminController(app, sql2o);
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("homepagetestdb");
        app.stop();
        driver.close();
    }

    @Test
    public void correctPage() {
        driver.get("http://localhost:7000/courses");
        assertEquals("NoteBook", driver.getTitle());
    }

    /**
     * Test if the sign in button from Google exists on the page
     */
    @Test
    public void signInExists() {
        driver.get("http://localhost:7000/courses");
        WebElement signInButton = driver.findElement(By.id("signin-btn"));
        assertEquals("g-signin2", signInButton.getAttribute("class"));
    }

    /**
     * Check that a course exists on the course list page
     *
     */
    @Test
    public void coursesAreListed() {
        driver.get("http://localhost:7000/courses");
        List<WebElement> links = driver.findElements(By.className("list-group"));
        WebElement exampleCourseOne = links.get(0);
        String courses = exampleCourseOne.getText();
        int count = 0;
        while (courses.charAt(count) != '1') {
            count++;
        }
        count++;
        String courseText = courses.substring(0, count);
        System.out.println(exampleCourseOne.getText());
        assertEquals("Example Course 1", courseText);
    }

    /**
     * Check that the correct page is loaded upon clicking on a course
     *
     */
    @Test
    public void courseLinkWorks() {
        driver.get("http://localhost:7000/courses");
        WebElement listItem = driver.findElement(By.className("list-group"));
        List<WebElement> links = listItem.findElements(By.tagName("a"));
        WebElement exampleCourseOneLink = links.get(0);
        exampleCourseOneLink.click();
        assertEquals("http://localhost:7000/courses/1/notes/", driver.getCurrentUrl());
    }

}
