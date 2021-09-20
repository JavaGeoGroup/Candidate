package ge.project.demo.service;

import java.io.File;

public interface EmailService {
  void sendSimpleEmail(String text, String receiver, String subject);
  void sendEmailAttachment(String subject, String message, File attachment);
}
