package com.example.flutter.util;

import com.example.flutter.entities.Mentor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

@Service
public class CommonFiles {

    @Autowired
    private JavaMailSender mailSender;

    public String generateAlphaPassword(int length) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        // Generate a password with the specified length
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(alphabet.length());
            password.append(alphabet.charAt(index));
        }
        return password.toString();
    }

    public String generateOTP(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public void sendPasswordToMentor(Mentor mentor, String password) {
        String emailBody = "Dear " + mentor.getName() + ",\n\n" +
                "Welcome to Mentor Boosters! We’re thrilled to have you join us as a mentor and look forward to your valuable contributions to our community.\n\n" +
                "Below are your login credentials for accessing our portal:\n\n" +
                "Email Id : " + mentor.getEmail() + "\n" +
                "Password : " + password + "\n\n" +
                "To log in, please visit: " + "https://www.mentorboosters.com/#/home " + "\n\n" +
                "For your security, we recommend updating your password upon your first login.\n\n" +
                "Thank you for joining our mission, and we look forward to working with you!\n\n" +
                "Best regards,\nMentor Boosters Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mentor.getEmail());
        message.setSubject("Welcome to MentorBoosters - Your Login Details");
        message.setText(emailBody);
        message.setFrom("admin@mentorboosters.com");
        mailSender.send(message);
    }

    public void sendOTPUser(String email, String otp) {
        String emailBody = "Dear User,\n\n"
                + "Thank you for registering with Mentor Boosters. To verify your email address, please use the One-Time Password (OTP) below:\n\n"
                + "OTP: " + otp + "\n\n"
                + "Please don't worry about this email if you did not initiate this request.\n\n"
                + "Best regards,\nThe Mentor Boosters Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verify Your Email Address");
        message.setText(emailBody);
        message.setFrom("admin@mentorboosters.com");
        mailSender.send(message);
    }

}
