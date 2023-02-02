package com.project.service.mail;

import com.project.helper.exception.RedditException;
import com.project.helper.payload.email.Email;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final MailContentBuilder mailContentBuilder;

    @Async
    public void sendEmail(Email email) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("learn.mike.helloworld@gmail.com");
            messageHelper.setTo(email.getRecipient());
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(mailContentBuilder.build(email.getBody()));
        };
        try {
            mailSender.send(messagePreparator);
            log.info("\n\n\t\t<<<<<<<<<< Activation Email Sent! >>>>>>>>>>");
        } catch (MailException e) {
            throw new RedditException("<<<<<<<<<< " +
                                      "Exception occurred while sending mail to \""
                                      + email.getRecipient() +
                                      "\" >>>>>>>>>>");
        }
    }
}