package com.forge_mind.forge_mind.Controller;

import com.forge_mind.forge_mind.Dto.LoginDto;
import com.forge_mind.forge_mind.Dto.Signup;
import com.forge_mind.forge_mind.Dto.VerifyDto;
import com.forge_mind.forge_mind.Service.AuthService;
import com.forge_mind.forge_mind.Service.UserService;
import com.forge_mind.forge_mind.Service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/")
public class userController {
    private UserService userService;
    private AuthService authService;
    private JwtService jwtService;

    public userController(UserService userService , AuthService authService, JwtService jwtService) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @GetMapping(path = "/allusers")
    public List ListAllUsers(){
        return List.of(userService.allUsers());
    }

    @PostMapping(path = "/signup")
    public String signup(@RequestBody Signup request){
        try {
            authService.signup(request.getUsername(), request.getEmail(), request.getPassword());
            return "Signup successful! Please check your email for verification code.";
        } catch (RuntimeException e) {
            return "Signup failed: " + e.getMessage();
        }
    }

    @PostMapping(path = "/signup/verify")
    public String verifyAndAuthenticate(@RequestBody VerifyDto verify) {
        try {
            return authService.checkVerification(verify.getEmail(), verify.getVerificationCode());
        } catch (RuntimeException e) {
            return "Verification failed: " + e.getMessage();
        }
    }


    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @GetMapping(path = "signup/verifyToken")
    public ResponseEntity<?> verifytoken(@RequestHeader("Authorization") String authheader){
        ResponseEntity<?> result = authService.verifyjwt(authheader);
        return result;
    }

}
