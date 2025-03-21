package com.example.SpringSecurityKrushuPfeBakcned.Services;

import com.example.SpringSecurityKrushuPfeBakcned.Dto.ChangePasswordEmailDto;
import com.example.SpringSecurityKrushuPfeBakcned.Dto.SendMailDto;
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
    public void sendPasswordChangeEmail(ChangePasswordEmailDto passwordChangeMailDto) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(passwordChangeMailDto.getEmail());
            helper.setSubject("Password Change Notification");
            helper.setFrom("m1ghtym3d@gmail.com");

            Context context = new Context();
            context.setVariable("name", passwordChangeMailDto.getName());
            context.setVariable("oldPassword", passwordChangeMailDto.getOldPassword());
            context.setVariable("newPassword", passwordChangeMailDto.getNewPassword());

            String htmlContent = springTemplateEngine.process("PasswordChange", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new MessagingException("Failed to send password change email", e);
        }
    }
    public void sendResetEmail(String email, String resetLink) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setFrom("m1ghtym3d@gmail.com");

            Context context = new Context();
            context.setVariable("resetLink", resetLink);

            String htmlContent = springTemplateEngine.process("PasswordReset", context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new MessagingException("Failed to send reset email", e);
        }
    }
}
