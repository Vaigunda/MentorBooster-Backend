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
                "Welcome to [Organization Name]! We’re thrilled to have you join us as a mentor and look forward to your valuable contributions to our community.\n\n" +
                "Below are your login credentials for accessing our portal:\n\n" +
                "Email Id : " + mentor.getEmail() + "\n" +
                "Password : " + password + "\n\n" +
                "To log in, please visit: " + "http://localhost:51030/#/home " + "\n\n" +
                "For your security, we recommend updating your password upon your first login.\n\n" +
                "Thank you for joining our mission, and we look forward to working with you!\n\n" +
                "Best regards,\nYour Application Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mentor.getEmail());
        message.setSubject("Welcome to MentorBoosters - Your Login Details");
        message.setText(emailBody);
        message.setFrom("padma220395@gmail.com");
        mailSender.send(message);
    }

    public void sendOTPUser(String email, String otp) {
        String emailBody = "Dear User,\n\n"
                + "Before you registered in Mentor Boosters, to complete your email verification, please use the following OTP code:\n\n"
                + "OTP: " + otp + "\n\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Best regards,\nYour Application Team";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Email Verification");
        message.setText(emailBody);
        message.setFrom("padma220395@gmail.com");
        mailSender.send(message);
    }

}
