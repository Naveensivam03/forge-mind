# YouTube-like Voting System Implementation Plan

## Current State Analysis
- Posts entity has `upvotes` and `downvotes` fields (Integer)
- User authentication system is in place
- No current tracking of individual user votes

## 1. Database Schema Changes

### New Entity: UserVote
```java
@Entity
@Table(name = "user_votes")
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Posts post;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### Enum: VoteType
```java
public enum VoteType {
    UPVOTE, DOWNVOTE
}
```

### Database Constraints
- Add unique constraint on (user_id, post_id) to prevent duplicate votes
- Add indexes for better query performance

## 2. Repository Layer

### UserVoteRepository
```java
@Repository
public interface UserVoteRepository extends JpaRepository<UserVote, Long> {
    Optional<UserVote> findByUserIdAndPostId(Long userId, Long postId);
    List<UserVote> findByUserId(Long userId);
    List<UserVote> findByPostId(Long postId);
    void deleteByUserIdAndPostId(Long userId, Long postId);
    long countByPostIdAndVoteType(Long postId, VoteType voteType);
}
```

## 3. Service Layer

### VotingService
```java
@Service
public class VotingService {
    
    // Vote on a post
    public VoteResponse voteOnPost(Long userId, Long postId, VoteType voteType);
    
    // Remove vote from a post
    public VoteResponse removeVote(Long userId, Long postId);
    
    // Get vote counts for a post
    public VoteCountResponse getVoteCounts(Long postId);
    
    // Get user's vote on a post
    public Optional<VoteType> getUserVote(Long userId, Long postId);
    
    // Get all votes by user
    public List<UserVote> getUserVotes(Long userId);
    
    // Update post vote counters (internal method)
    private void updatePostVoteCounters(Long postId);
}
```

## 4. Controller Layer

### VotingController
```java
@RestController
@RequestMapping("/api/votes")
public class VotingController {
    
    @PostMapping("/posts/{postId}/vote")
    public ResponseEntity<VoteResponse> voteOnPost(
        @PathVariable Long postId,
        @RequestBody VoteRequest request,
        Authentication authentication
    );
    
    @DeleteMapping("/posts/{postId}/vote")
    public ResponseEntity<VoteResponse> removeVote(
        @PathVariable Long postId,
        Authentication authentication
    );
    
    @GetMapping("/posts/{postId}/counts")
    public ResponseEntity<VoteCountResponse> getVoteCounts(
        @PathVariable Long postId
    );
    
    @GetMapping("/posts/{postId}/my-vote")
    public ResponseEntity<UserVoteResponse> getMyVote(
        @PathVariable Long postId,
        Authentication authentication
    );
}
```

## 5. DTOs

### VoteRequest
```java
public class VoteRequest {
    private VoteType voteType;
    // getters and setters
}
```

### VoteResponse
```java
public class VoteResponse {
    private boolean success;
    private String message;
    private Long postId;
    private VoteType previousVote;
    private VoteType currentVote;
    private VoteCountResponse counts;
    // getters and setters
}
```

### VoteCountResponse
```java
public class VoteCountResponse {
    private Long postId;
    private Integer upvotes;
    private Integer downvotes;
    private Integer totalVotes;
    private Double voteRatio; // upvotes / (upvotes + downvotes)
    // getters and setters
}
```

## 6. Business Logic Implementation

### Vote Processing Algorithm
1. Check if user has already voted on this post
2. If no previous vote:
   - Create new UserVote record
   - Increment appropriate counter in Posts
3. If same vote type:
   - Remove UserVote record
   - Decrement appropriate counter in Posts
4. If different vote type:
   - Update UserVote record
   - Decrement old counter, increment new counter in Posts

### Vote Counter Consistency
- Use database transactions to ensure consistency
- Consider using database triggers for automatic counter updates
- Implement periodic reconciliation job to fix any inconsistencies

## 7. Security Considerations

### Authentication & Authorization
- Only authenticated users can vote
- Users can only vote once per post
- Users can only modify their own votes
- Rate limiting to prevent spam voting

### Data Integrity
- Validate vote type values
- Ensure post exists before allowing votes
- Prevent voting on deleted posts

## 8. Performance Optimizations

### Database
- Index on (user_id, post_id) for quick lookups
- Index on post_id for counting votes
- Consider denormalization for high-traffic scenarios

### Caching
- Cache vote counts for popular posts
- Cache user vote status for recent posts
- Use Redis for real-time vote updates

## 9. API Endpoints Summary

```
POST   /api/votes/posts/{postId}/vote        # Vote on post
DELETE /api/votes/posts/{postId}/vote        # Remove vote
GET    /api/votes/posts/{postId}/counts      # Get vote counts
GET    /api/votes/posts/{postId}/my-vote     # Get my vote status
GET    /api/votes/my-votes                   # Get all my votes
```

## 10. Frontend Integration

### JavaScript API Calls
```javascript
// Vote on post
async function voteOnPost(postId, voteType) {
    const response = await fetch(`/api/votes/posts/${postId}/vote`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ voteType })
    });
    return response.json();
}

// Remove vote
async function removeVote(postId) {
    const response = await fetch(`/api/votes/posts/${postId}/vote`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    return response.json();
}
```

### UI Components
- Vote buttons (up/down arrows)
- Vote count display
- User vote status indicator
- Loading states during API calls

## 11. Testing Strategy

### Unit Tests
- VotingService business logic
- Vote counting accuracy
- Edge cases (duplicate votes, invalid data)

### Integration Tests
- API endpoint functionality
- Database consistency
- Authentication/authorization

### Load Testing
- High-volume voting scenarios
- Concurrent vote updates
- Database performance under load

## 12. Migration Strategy

### Database Migration
1. Create UserVote table
2. Add indexes
3. Populate existing vote counts (if any)

### Rollback Plan
- Keep old vote counting logic as backup
- Ability to switch back if issues arise

## 13. Monitoring & Analytics

### Metrics to Track
- Vote activity per post
- User engagement rates
- Vote distribution patterns
- System performance metrics

### Logging
- All vote actions
- Performance metrics
- Error tracking

This implementation provides a robust, scalable voting system similar to YouTube's like/dislike functionality, with proper data integrity, security, and performance considerations.
