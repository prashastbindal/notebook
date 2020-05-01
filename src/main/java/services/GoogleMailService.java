package services;

import io.javalin.Javalin;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

// We are sending just one email with all subscribers in BCC due to the limitation of our free Google SMTP server.
// If we sent multiple emails to each subscribers individually, we would exceed our quota and the SMTP server would flag us as spammer
public class GoogleMailService implements EmailService {
    private String userName;
    private String password;
    private final String SMTP_SERVER = "smtp.gmail.com";
    private final String SMTP_PORT = "587";
    private Session session;

    public GoogleMailService(){
        userName = System.getenv("USERNAME");
        password = System.getenv("PASSWORD");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password);
                    }
                });
    }

    /**
     * Send email with the passed arguments for body, list of recipient email ids, and email subject
     *
     * @param body : body of the email
     * @param recipients: list of email addresses of recipients
     * @param subject : the subject of the email
     */
    public void sendEmail (String body, List<String> recipients, String subject) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(userName));
            message.setRecipients(
                    Message.RecipientType.BCC,
                    InternetAddress.parse(String.join(",", recipients))
            );

            message.setSubject(subject);
            message.setContent(body, "text/html");
            Transport.send(message);

        } catch (MessagingException e) {
            Javalin.log.error("Exception raised while sending email:" +
                    "\nRecipients: "+ recipients.toString() +
                    "\nSubject: "+ subject +
                    "\nBody: "+ body, e);
        }
    }
}
