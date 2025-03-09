package com.example.SpringSecurity.Services;

import com.example.SpringSecurity.Dto.SendMailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
        private final SpringTemplateEngine springTemplateEngine;
    @Autowired
    public EmailService(JavaMailSender javaMailSender, SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine = springTemplateEngine;
    }
    public void sendEmail(SendMailDto sendMailDto) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(sendMailDto.getEmail());
            helper.setSubject("Account Registration Successful");
            helper.setFrom("m1ghtym3d@gmail.com");

            Context context = new Context();
            context.setVariable("name", sendMailDto.getName());
            context.setVariable("email", sendMailDto.getEmail());
            context.setVariable("password", sendMailDto.getPassword());

            String htmlContent = springTemplateEngine.process("SignUp", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new MessagingException("Failed to send verification email", e);
        }
    }
}
