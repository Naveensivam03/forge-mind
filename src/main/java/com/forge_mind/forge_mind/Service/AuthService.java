package com.forge_mind.forge_mind.Service;

import com.forge_mind.forge_mind.Repository.UserRepository;
import com.forge_mind.forge_mind.database.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

  private UserRepository userRepository;
  private JwtService jwtService;
  private EmailService emailService;
  private PasswordEncoder passwordEncoder;

  public AuthService(UserRepository userRepository, JwtService jwtService, EmailService emailService,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtService = jwtService;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
  }

  // authentication service for the signup:
  public void signup(String username, String email, String password) {
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isPresent()) {
      throw new RuntimeException("User with email " + email + " already exists!");
    } else {
      String hashedPassword = passwordEncoder.encode(password);
      User newUser = new User(username, email, hashedPassword);
      newUser.setVerificationCode(verificationCode());
      newUser.setAccount_enabled(false);
      User savedUser = userRepository.save(newUser);

      sendVerificationEmail(savedUser);
    }
  }

  // mail sending for verification
  public void sendVerificationEmail(User user) {
    String subject = "Account verification!";
    String verificationCode = user.getVerificationCode();
    String htmlMessage = "<html>"
        + "<body style=\"font-family: Arial, sans-serif;\">"
        + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
        + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
        + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
        + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
        + "<h3 style=\"color: #333;\">Verification Code:</h3>"
        + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
        + "</div>"
        + "</div>"
        + "</body>"
        + "</html>";
    try {
      emailService.SendVerifcationMail(user.getEmail(), subject, htmlMessage);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  private String verificationCode() {
    Random random = new Random();
    int code = random.nextInt(900000) + 100000;
    return String.valueOf(code);
  }

  public String checkVerification(String email, String verificationCode) {
    if (email == null || email.trim().isEmpty()) {
      throw new RuntimeException("Email cannot be null or empty!");
    }
    if (verificationCode == null || verificationCode.trim().isEmpty()) {
      throw new RuntimeException("Verification code cannot be null or empty!");
    }

    Optional<User> userOptional = userRepository.findByEmail(email.trim());
    if (userOptional.isPresent()) {
      User user = userOptional.get();

      if (user.isAccount_enabled()) {
        return "User is already verified!";
      }

      if (user.getVerificationCode() != null && user.getVerificationCode().equals(verificationCode.trim())) {
        user.setAccount_enabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);
        return "Account verified successfully!";
      } else {
        return "Invalid verification code!";
      }

    } else {
      return "User with this email does not exist!";
    }
  }
}

