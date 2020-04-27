package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

public class GoogleMailService implements EmailService {
    private final String USER_NAME = "notebooksoftware123@gmail.com";
    private final String PASSWORD = "notesoft123";
    private final String SMTP_SERVER = "smtp.gmail.com";
    private final String SMTP_PORT = "587";
    Session session;

    public GoogleMailService(){
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_SERVER);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER_NAME, PASSWORD);
                    }
                });
    }

    public void sendEmail (String body, List<String> recipients, String subject) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER_NAME));
            message.setRecipients(
                    Message.RecipientType.BCC,
                    InternetAddress.parse(String.join(",", recipients))
            );

//            for(String recipient : recipients){
//                message.addRecipient(
//                        Message.RecipientType.BCC,
//                        InternetAddress.parse(recipient)[0]
//                );
//            }

            message.setSubject(subject);
//            message.setText(body);
            message.setContent(body, "text/html");
            Transport.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
