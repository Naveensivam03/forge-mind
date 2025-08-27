package com.forge_mind.forge_mind.Service;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.forge_mind.forge_mind.Repository.UserVoteRepository;
import com.forge_mind.forge_mind.database.UserVotes;
import com.forge_mind.forge_mind.database.User;
import com.forge_mind.forge_mind.database.Posts;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Service
@Getter
@Setter
public class VoteService {

  private UserVoteRepository userVoteRepository;

  public VoteService(UserVoteRepository userVoteRepository) {
    this.userVoteRepository = userVoteRepository;
  }

  public List<UserVotes> retrieveAlllikedUser(long id) {
    List<UserVotes> posts = userVoteRepository.findByUserId(id);
    return posts;
  }
  
  @Transactional
  public String handleUserVote(User user, Posts post, String voteType) {
    Optional<UserVotes> existingVote = userVoteRepository.findByUserIdAndPostsId(user.getId(), post.getId());
    
    UserVotes.VoteType newVoteType = "up".equals(voteType) ? UserVotes.VoteType.UPVOTE : UserVotes.VoteType.DOWNVOTE;
    
    if (existingVote.isPresent()) {
      UserVotes existing = existingVote.get();
      
      // If user clicked the same vote type, remove the vote (toggle off)
      if (existing.getVoteType() == newVoteType) {
        userVoteRepository.delete(existing);
        // Update post vote counts
        if (newVoteType == UserVotes.VoteType.UPVOTE) {
          post.setUpvotes(post.getUpvotes() - 1);
        } else {
          post.setDownvotes(post.getDownvotes() - 1);
        }
        return "vote_removed";
      } else {
        // User changed their vote type
        // Remove old vote count and add new vote count
        if (existing.getVoteType() == UserVotes.VoteType.UPVOTE) {
          post.setUpvotes(post.getUpvotes() - 1);
          post.setDownvotes(post.getDownvotes() + 1);
        } else {
          post.setDownvotes(post.getDownvotes() - 1);
          post.setUpvotes(post.getUpvotes() + 1);
        }
        
        // Update the existing vote
        existing.setVoteType(newVoteType);
        userVoteRepository.save(existing);
        return "vote_changed";
      }
    } else {
      // New vote
      UserVotes newVote = new UserVotes(user, post, newVoteType);
      userVoteRepository.save(newVote);
      
      // Update post vote counts
      if (newVoteType == UserVotes.VoteType.UPVOTE) {
        post.setUpvotes(post.getUpvotes() + 1);
      } else {
        post.setDownvotes(post.getDownvotes() + 1);
      }
      return "vote_added";
    }
  }
  
  public Map<Long, String> getUserVoteMap(long userId) {
    List<UserVotes> userVotes = userVoteRepository.findByUserId(userId);
    Map<Long, String> voteMap = new HashMap<>();
    
    for (UserVotes vote : userVotes) {
      String voteTypeString = vote.getVoteType() == UserVotes.VoteType.UPVOTE ? "up" : "down";
      voteMap.put(vote.getPosts().getId(), voteTypeString);
    }
    
    return voteMap;
  }

}
