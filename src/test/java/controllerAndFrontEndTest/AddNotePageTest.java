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
import org.openqa.selenium.support.ui.Select;
import org.sql2o.Sql2o;
import java.io.File;
import java.util.List;

import static org.junit.Assert.*;


public class AddNotePageTest {
    private NoteDao noteDao;
    private CourseDao courseDao;
    private CommentDao commentDao;
    private WebDriver driver;

    @Before
    public void setup() throws Exception {

        DBBuilder.createTestDatabase("addnotepagetest");
        Sql2o sql2o = DBBuilder.getTestDatabaseConnection("addnotepagetest");
        DBBuilder.createTables(sql2o, true);
        DBBuilder.createTestData(sql2o, true);

        this.courseDao = new CourseDao(sql2o);
        this.noteDao = new NoteDao(sql2o);
        this.commentDao = new CommentDao(sql2o);
        Gson gson = new Gson();
        JavalinJson.setToJsonMapper(gson::toJson);
        JavalinJson.setFromJsonMapper(gson::fromJson);

        final int PORT = Integer.parseInt(System.getenv("PORT"));

        if (!new File("static/").exists()) {
            new File("static/").mkdirs();
        }

        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("static/", Location.EXTERNAL);
        }).start(PORT);

        app.get("/", ctx -> ctx.redirect("/courses"));

        Controller coursesPageHandler = new CourseListController(app, sql2o);
        Controller coursePageHandler = new CourseController(app, sql2o);
        Controller notePageHandler = new NoteController(app, sql2o);
        System.setProperty("webdriver.chrome.driver", "src\\test\\java\\controllerAndFrontEndTest\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @After
    public void tearDown() throws Exception {
        DBBuilder.deleteTestDatabase("addnotepagetest");
    }

    @Test
    public void webComponentsExist() {
        driver.get("http://localhost:7000/courses/1/addNote/");
        boolean missingComponents = false;
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        if (inputs.size() != 3) {
            missingComponents = true;
        }

        List<WebElement> select = driver.findElements(By.tagName("select"));
        if (select.size() != 1) {
            missingComponents = true;
        }

        List<WebElement> textarea = driver.findElements(By.tagName("textarea"));
        if (select.size() != 1) {
            missingComponents = true;
        }

        List<WebElement> submitButton = driver.findElements(By.tagName("button"));
        if (select.size() != 1) {
            missingComponents = true;
        }

        assertEquals(false, missingComponents);
    }

    @Test
    public void addNoteWithTextTest() {
        driver.get("http://localhost:7000/courses/1/addNote/");
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement inputTitle = inputs.get(0);
        WebElement inputName = inputs.get(1);
        inputTitle.sendKeys("Good Notes");
        inputName.sendKeys("Student 1");
        Select select = new Select(driver.findElement(By.tagName("select")));
        select.selectByVisibleText("Text");
        WebElement submitKey = driver.findElement(By.id("submit"));
        submitKey.click();
        List<WebElement> courses = driver.findElements(By.tagName("li"));
        WebElement newCourse = courses.get(2);
        assertEquals("Good Notes by Student 1", newCourse.getText());
    }

    @Test
    public void addNoteWithPDFTest() {
        driver.get("http://localhost:7000/courses/1/addNote/");
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        WebElement inputTitle = inputs.get(0);
        WebElement inputName = inputs.get(1);
        inputTitle.sendKeys("Good Notes");
        inputName.sendKeys("Student 1");
        Select select = new Select(driver.findElement(By.tagName("select")));
        select.selectByVisibleText("PDF");
        WebElement submitKey = driver.findElement(By.tagName("button"));
        submitKey.click();
        List<WebElement> courses = driver.findElements(By.tagName("li"));
        WebElement newCourse = courses.get(2);
        assertEquals("Good Notes by Student 1", newCourse.getText());
    }
}
