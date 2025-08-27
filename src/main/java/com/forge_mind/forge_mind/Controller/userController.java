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
import com.forge_mind.forge_mind.Service.VoteService;
import com.forge_mind.forge_mind.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.forge_mind.forge_mind.database.Category;
import com.forge_mind.forge_mind.database.Posts;
import com.forge_mind.forge_mind.database.User;

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
  private VoteService voteService;
  private UserRepository userRepository;

  public userController(UserService userService, AuthService authService, JwtService jwtService,
      PostService postService, CategRepository categRepository, VoteService voteService, UserRepository userRepository) {
    this.userService = userService;
    this.authService = authService;
    this.jwtService = jwtService;
    this.postService = postService;
    this.categRepository = categRepository;
    this.voteService = voteService;
    this.userRepository = userRepository;
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
    System.out.println("Login endpoint hit with email: " + loginRequest.getEmail());
    ResponseEntity<?> result = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
    System.out.println("Login result status: " + result.getStatusCode());
    return result;
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
    try {
      String username = principal.getName();
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new RuntimeException("User not found"));
      
      Posts post = postService.getpost(id);
      if (post == null) {
        return ResponseEntity.badRequest().body("Post not found");
      }
      
      String result = voteService.handleUserVote(user, post, interact);
      postService.save(post); // Save the updated vote counts
      
      return ResponseEntity.ok(Map.of(
          "message", "Vote processed successfully",
          "result", result,
          "upvotes", post.getUpvotes(),
          "downvotes", post.getDownvotes()
      ));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error processing vote: " + e.getMessage());
    }
  }
  
  @GetMapping(path = "/signup/user-votes")
  public ResponseEntity<?> getUserVotes(Principal principal) {
    try {
      if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("User not authenticated");
      }
      
      String username = principal.getName();
      System.out.println("Getting votes for user: " + username);
      
      User user = userRepository.findByUsername(username)
          .orElseThrow(() -> new RuntimeException("User not found: " + username));
      
      Map<Long, String> userVotes = voteService.getUserVoteMap(user.getId());
      System.out.println("Found " + userVotes.size() + " votes for user " + username);
      
      return ResponseEntity.ok(userVotes);
    } catch (Exception e) {
      System.err.println("Error fetching user votes: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", "Error fetching user votes: " + e.getMessage()));
    }
  }

}
