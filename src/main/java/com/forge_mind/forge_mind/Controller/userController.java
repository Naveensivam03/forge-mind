package com.forge_mind.forge_mind.Controller;

import com.forge_mind.forge_mind.Dto.LoginDto;
import com.forge_mind.forge_mind.Dto.PostResponseDto;
import com.forge_mind.forge_mind.Dto.Signup;
import com.forge_mind.forge_mind.Dto.VerifyDto;
import com.forge_mind.forge_mind.Repository.CategRepository;
import com.forge_mind.forge_mind.Service.AuthService;
import com.forge_mind.forge_mind.Service.PostService;
import com.forge_mind.forge_mind.Service.UserService;
import com.forge_mind.forge_mind.Service.JwtService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.forge_mind.forge_mind.database.Category;
import java.util.List;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping(path = "/")
public class userController {
    private UserService userService;
    private AuthService authService;
    private JwtService jwtService;
    private PostService postService;
    private CategRepository categRepository;

    public userController(UserService userService, AuthService authService, JwtService jwtService,
            PostService postService, CategRepository categRepository) {
        this.userService = userService;
        this.authService = authService;
        this.jwtService = jwtService;
        this.postService = postService;
        this.categRepository = categRepository;
    }

    @GetMapping(path = "/allusers")
    public List ListAllUsers() {
        return List.of(userService.allUsers());
    }

    @PostMapping(path = "/signup")
    public String signup(@RequestBody Signup request) {
        try {
            authService.signup(request.getUsername(), request.getEmail(), request.getPassword());
            System.out.println("Signup successful! Please check your email for verification code.");
            return "Signup successful! Please check your email for verification code.";
        } catch (RuntimeException e) {
            return "Signup failed: " + e.getMessage();
        }
    }

    @PostMapping(path = "/signup/verify")
    public ResponseEntity<String> verifyAndAuthenticate(@RequestBody VerifyDto verify) {
        try {
            return ResponseEntity.ok(authService.checkVerification(verify.getEmail(), verify.getVerificationCode()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Verification failed: " + e.getMessage());
        }
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginRequest) {
        return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @GetMapping(path = "signup/verifyToken")
    public ResponseEntity<?> verifytoken(@RequestHeader("Authorization") String authheader) {
        ResponseEntity<?> result = authService.verifyjwt(authheader);
        return result;
    }

    @GetMapping(path = "/signup/all")
    public List<PostResponseDto> allposts() {
        List<PostResponseDto> allposts = postService.allPosts();

        System.out.println("done sending the posts.");
        System.out.println(allposts);
        return allposts;
    }

    @GetMapping(path = "/signup/allposts/{name}")
    public List catePosts(@PathVariable String name) {
        return postService.categPosts(name);
    }

    @GetMapping(path = "/signup/allcateg")
    public List<Category> allcategory() {
        System.out.println("hello");
        return (categRepository.findAll());
    }
}
