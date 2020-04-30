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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.Select;
import org.sql2o.Sql2o;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.*;
import static org.junit.Assert.*;


public class AddNotePageTest {
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

        final int PORT = 7000;

        if (! new File("static/").exists()) {
            new File("static/").mkdirs();
        }

        app = Javalin.create(config -> {
            config.addStaticFiles("static/", Location.EXTERNAL);
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
        DBBuilder.deleteTestDatabase("addnotepagetest");
        app.stop();
        driver.close();
    }

    @Test
    public void webComponentsExist() {
        driver.get("http://localhost:7000/courses/1/addNote/");
        boolean missingComponents = false;
        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        if (inputs.size() != 4) {
            missingComponents = true;
        }

        List<WebElement> select = driver.findElements(By.tagName("select"));
        if (select.size() != 6) {
            missingComponents = true;
        }

        List<WebElement> textarea = driver.findElements(By.tagName("textarea"));
        if (textarea.size() != 4) {
            missingComponents = true;
        }

        List<WebElement> submitButton = driver.findElements(By.tagName("button"));
        if (submitButton.size() != 22) {
            missingComponents = true;
        }

        assertEquals(false, missingComponents);
    }

    /*
    @Test
    public void addNoteWithTextTest() {
        WebElement inputTitle = driver.findElement(By.id("title-field"));
        inputTitle.click();
        inputTitle.sendKeys("Good Notes");

        Select select = new Select(driver.findElement(By.id("filetype-field")));
        select.selectByVisibleText("Text");

        WebElement inputTextfield = driver.findElement(By.id("texteditor"));
        inputTextfield.sendKeys("Note content HTML");


        WebElement submitKey = driver.findElement(By.name("submit"));
        submitKey.sendKeys(Keys.RETURN);

        System.out.println(driver.getCurrentUrl());

        //List<WebElement> notes = driver.findElements(By.className("note-select"));
        //WebElement newNote = notes.get(2);
        assertEquals("Good Notes by\nUpvotes: 0", "");
    }

    public void addNoteWithPDFTest() {
        driver.get("http://localhost:7000/courses/1/addNote/");

        WebElement inputTitle = driver.findElement(By.id("title-field"));

        inputTitle.sendKeys("Good Notes");
        Select select = new Select(driver.findElement(By.id("filetype-field")));
        select.selectByVisibleText("PDF");

        WebElement submitKey = driver.findElement(By.id("submit-button"));
        submitKey.click();

        List<WebElement> notes = driver.findElements(By.className("note-select"));
        WebElement newNote = notes.get(0);
        assertEquals("Good Notes by\nUpvotes: 0", newNote.getText());
    } */
}
