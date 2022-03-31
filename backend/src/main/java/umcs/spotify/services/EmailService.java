package umcs.spotify.services;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import umcs.spotify.entity.User;

import javax.mail.MessagingException;
import java.io.IOException;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendEmailVerificationEmail(User user) {
        try {
            var message = emailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);

            helper.setTo(user.getEmail());
            helper.setSubject("[Spotify 2.0] Email verification");
            helper.setText("Your verification code: " + user.getEmailConfirmationCode());
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
