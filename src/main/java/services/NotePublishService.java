package services;

import dao.CourseDao;
import dao.NoteDao;
import dao.SubscriptionDao;
import fileserver.FileServer;
import io.javalin.http.UploadedFile;
import model.Note;
import model.Subscription;
import org.apache.commons.collections.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class NotePublishService{
    private FileServer fileServer;
    private OCRTextExtractor textExtractor;
    private NoteDao noteDao;
    private SubscriptionDao subscriptionDao;
    private CourseDao courseDao;
    private EmailService emailService;

    public NotePublishService(NoteDao noteDao, SubscriptionDao subscriptionDao, CourseDao courseDao){
        this.fileServer = FileServer.getFileServer();
        this.textExtractor = new TesseractTextExtractor();
        this.noteDao = noteDao;
        this.subscriptionDao = subscriptionDao;
        this.courseDao = courseDao;
        this.emailService = new GoogleMailService();

    }

    public void publishNote(Note note, UploadedFile file, String text){
        if (note.getFiletype().equals("pdf")) {
            assert file != null;
            fileServer.upload(file.getContent(), note);

            Thread thread = new Thread(() -> {
                String path = fileServer.getLocalFile(note);
                String fulltext = textExtractor.extractText(path);
                if (fulltext != null) {
                    note.setFulltext(fulltext);
                    noteDao.updateFulltext(note);
                }
            });
            thread.start();
        } else if (note.getFiletype().equals("html")) {
            assert text != null;
            InputStream textstream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

            text = text.replaceAll("\\<.*?\\>", "");
            note.setFulltext(text);
            noteDao.updateFulltext(note);

            fileServer.upload(textstream, note);
        } else if (note.getFiletype().equals("md")) {
            assert text != null;
            InputStream textstream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));

            note.setFulltext(text);
            noteDao.updateFulltext(note);

            fileServer.upload(textstream, note);
        }


        String body = "<html> <body> <p>Dear Notebooker, <br><br>"+
                "There is a new note titled <b>" + note.getTitle() + "</b> uploaded for the course <b>" +
                courseDao.findCourse(note.getCourseId()).getName() + "</b>. Do not forget to check it out !" +
                "<br><br> Your Study Partner <br> Team Notebook </p></body></html>";

        String subject = "Suggested new note for you";

        List<Subscription> subscription = subscriptionDao.findSubscriptoinsWithCourseId(note.getCourseId());
        if(!CollectionUtils.isEmpty(subscription)){
            List<String> recipients = subscription.stream().map(x -> x.getUserEmail()).collect(Collectors.toList());
            emailService.sendEmail(body, recipients, subject);
        }




    }

}
