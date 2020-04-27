package services;

import java.util.List;

public interface EmailService {
    public void sendEmail (String body, List<String> recipients, String subject);
}
