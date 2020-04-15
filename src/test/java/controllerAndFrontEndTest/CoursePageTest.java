package controllerAndFrontEndTest;

import com.amazonaws.services.dynamodbv2.xspec.S;
import com.google.gson.Gson;
import controller.*;
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
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.sql2o.Sql2o;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.*;
import static org.junit.Assert.*;


public class CoursePageTest {
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

        DBBuilder.createTestDatabase("coursepagetestdb");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("coursepagetestdb");
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

        Controller staticPagesHandler = new StaticController(app, sql2o);
        Controller coursesPageHandler = new CourseListController(app, sql2o);
        Controller coursePageHandler  = new CourseController(app, sql2o);
        Controller notePageHandler    = new NoteController(app, sql2o);
        Controller adminPageHandler   = new AdminController(app, sql2o);
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("coursepagetestdb");
        app.stop();
        driver.close();
    }

    @Test
    public void correctPage() {
        driver.get("http://localhost:7000/courses/1/notes/");
        WebElement h2 = driver.findElement(By.id("course-name-header"));
        assertEquals("Example Course 1", h2.getText());
    }

    @Test
    public void addNoteExists() {
        driver.get("http://localhost:7000/courses/1/notes/");
        WebElement addNote = driver.findElement(By.id("add-note-button"));
        assertNotNull(addNote);
        assertEquals("Add a Note", addNote.getText());
    }

    @Test
    public void addNoteLinkWorks() {
        driver.get("http://localhost:7000/courses/1/notes/");
        WebElement addNote = driver.findElement(By.id("add-note-button"));
        addNote.click();
        assertEquals("http://localhost:7000/courses/1/notes/", driver.getCurrentUrl());
    }

    @Test
    public void notesAreListed() {
        driver.get("http://localhost:7000/courses/1/notes/");
        List<WebElement> links = driver.findElements(By.className("note-select"));
        WebElement note1 = links.get(0);
        assertEquals("Note1 by student1\nUpvotes: 0", note1.getText());
    }

    @Test
    public void notesLinksWork() {
        driver.get("http://localhost:7000/courses/1/notes/");
        List<WebElement> links = driver.findElements(By.className("note-select"));
        WebElement note1 = links.get(0);
        note1.click();
        WebElement frame = driver.findElement(By.id("note-frame"));
        assertEquals("http://localhost:7000/courses/1/notes-preview/1", frame.getAttribute("src"));
    }

    @Test
    public void noteUpvoteExists() {
        driver.get("http://localhost:7000/courses/1/notes/1");
        WebElement upvoteForm = driver.findElement(By.id("upvote-form"));
        assertEquals("Upvote: 0", upvoteForm.getText());
    }

    /*
    @Test
    public void noteUpvoteWorks() {
        driver.get("http://localhost:7000/courses/1/notes/1");
        WebElement upvoteForm = driver.findElement(By.id("upvote-form"));
        upvoteForm.submit();
        WebElement upvoteButton = driver.findElement(By.id("upvote"));

        driver.get("http://localhost:7000/courses/1/notes/1/upvote");
        assertEquals("Upvote: 1", upvoteButton.getText());
    }  */

    @Test
    public void commentsExist() {
        driver.get("http://localhost:7000/courses/1/notes/1");
        WebElement comment = driver.findElement(By.id("comment1"));
        assertEquals("student 1\nthis is a comment\nReply",comment.getText());
    }

}