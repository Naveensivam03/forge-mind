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
    // check whether the email already exists or not
    Optional<User> user = userRepository.findByEmail(email);

    if (user.isPresent()) {
      throw new RuntimeException("User with email " + email + " already exists!");
    } else {
      // Hash the password before saving
      String hashedPassword = passwordEncoder.encode(password);
      User newUser = new User(username, email, hashedPassword);
      newUser.setVerificationCode(verificationCode());
      newUser.setAccount_enabled(false); // Initially disabled until verified
      User savedUser = userRepository.save(newUser);

      // Send verification email with the saved user data
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
    // Validate input parameters
    if (email == null || email.trim().isEmpty()) {
      throw new RuntimeException("Email cannot be null or empty!");
    }
    if (verificationCode == null || verificationCode.trim().isEmpty()) {
      throw new RuntimeException("Verification code cannot be null or empty!");
    }

    Optional<User> userOptional = userRepository.findByEmail(email.trim());
    if (userOptional.isPresent()) {
      User user = userOptional.get();

      // Check if user is already verified
      if (user.isAccount_enabled()) {
        return "User is already verified!";
      }

      // Check if verification code matches
      if (user.getVerificationCode() != null && user.getVerificationCode().equals(verificationCode.trim())) {
        // Enable the account
        user.setAccount_enabled(true);
        // Clear the verification code for security
        user.setVerificationCode(null);
        userRepository.save(user);
        String jwtToken = jwtService.generateTokens(user);
        // System.out.println("User found: " + userOptional.get().getUsername());
      User user = userOptional.get();

      // Check if account is verified
      if (!user.isAccount_enabled()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Account not verified. Please verify your email first.");
      }

      // Check if password matches
      if (passwordEncoder.matches(password, user.getPassword())) {
        // Generate JWT token for successful login
        String jwtToken = jwtService.generateTokens(user);

        return ResponseEntity.ok(Map.of(
            "message", "Login successful!",
            "token", jwtToken,
            "username", user.getUsername(),
            "email", user.getEmail()
        ));
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid password!");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("User with this email does not exist!");
    }
  }

  // // Inner class for login response
  // public static class LoginResponse {
  // private String message;
  // private String token;
  // private String username;
  // private String email;
  //
  // public LoginResponse(String message, String token, String username, String
  // email) {
  // this.message = message;
  // this.token = token;
  // this.username = username;
  // this.email = email;
  // }
  //
  // // Getters
  // public String getMessage() { return message; }
  // public String getToken() { return token; }
  // public String getUsername() { return username; }
  // public String getEmail() { return email; }
  // }

}
