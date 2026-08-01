package com.meetingintel.meeting_intel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendActionItemReminder(String toEmail, String task, String dueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Action Item Reminder: " + task);
        message.setText(
                "Hi,\n\n" +
                        "This is a reminder for your pending action item:\n\n" +
                        "Task: " + task + "\n" +
                        "Due Date: " + dueDate + "\n\n" +
                        "Please make sure to complete it on time.\n\n" +
                        "Meeting Intel"
        );
        mailSender.send(message);
    }
}