package ge.project.demo.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService {

  @Value("${spring.mail.username}")
  private String EMAIL;

  @Value("${report.receiver.email}")
  private String reportReceiver;

  private JavaMailSender emailSender;

  @Autowired
  public EmailServiceImpl(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }

  @Override
  public void sendSimpleEmail(String text, String receiver, String subject) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(EMAIL);
    message.setTo(receiver);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  @Override
  public void sendEmailAttachment(String subject, String message, File attachment) {
    try{
      MimeMessage mimeMessage = emailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
      helper.setFrom(EMAIL);
      helper.setTo(reportReceiver);
      helper.setSubject(subject);
      helper.setText(message);
      FileSystemResource file = new FileSystemResource(attachment);
      helper.addAttachment(attachment.getName(), file);
      emailSender.send(mimeMessage);
      log.info("Email sending complete.");
    } catch (Exception e){
      log.error(e.getMessage(), e);
    }

  }
}
