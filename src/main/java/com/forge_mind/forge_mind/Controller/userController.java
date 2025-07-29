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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.forge_mind.forge_mind.database.Category;
import com.forge_mind.forge_mind.database.Posts;

import java.security.Principal;
import java.util.List;
import java.util.Map;
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
  public ResponseEntity<Map<String, String>> verifyAndAuthenticate(@RequestBody VerifyDto verify) {
    try {
      String token = authService.checkVerification(verify.getEmail(), verify.getVerificationCode());
      return ResponseEntity.ok(Map.of("token", token));
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Verification failed: " + e.getMessage()));
    }
  }

  @PostMapping(path = "/login")
  public ResponseEntity<?> login(@RequestBody LoginDto loginRequest) {
    return authService.login(loginRequest.getEmail(), loginRequest.getPassword());
  }

  @GetMapping(path = "signup/verifyToken")
  public String verifytoken(@RequestHeader("Authorization") String authheader) {
    String result = authService.verifyjwt(authheader);
    return result;
  }

  @GetMapping(path = "/signup/all")
  public ResponseEntity<?> allposts(Principal principal) {
    try {
//      String authHeader = request.getHeader("Authorization");
//
//      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//            .body("Missing or invalid Authorization header");
//      }
//
//      String token = authHeader.substring(7); // Remove "Bearer "
//
//      // Call your authService to validate the token
//      authService.verifyjwt(token); // Assume this throws if invalid
      String email = principal.getName();
      List<PostResponseDto> allPosts = postService.allPosts();
      return ResponseEntity.ok(allPosts);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Invalid or expired token: " + e.getMessage());
    }
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

  @PostMapping(path = "/signup/interact")
  public ResponseEntity<?> interact(@RequestParam long id, @RequestParam String interact, Principal principal) {
//    String authHeader = request.getHeader("Authorization");
//
//    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//          .body("Missing or invalid Authorization header");
//    }
//
//    String token = authHeader.substring(7); // Remove "Bearer "
//
//    // Call your authService to validate the token
//    authService.verifyjwt(token);
    String email = principal.getName();
    Posts post = postService.getpost(id);
    Posts p = post;
    if ("up".equals(interact)) {
      int upvotes = p.getUpvotes();
      p.setUpvotes(upvotes + 1);
    } else if ("down".equals(interact)) {
      int down = p.getDownvotes();
      p.setDownvotes(down + 1);
    }
    postService.save(post);

    return ResponseEntity.ok("posts not found");
  }

}
