package com.forge_mind.forge_mind.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;


    public void SendVerifcationMail(String to  , String subject , String text) throws MessagingException{
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage , true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text , true);
        javaMailSender.send(mimeMessage);
        // System.out.println("fininshed");
    }
}
