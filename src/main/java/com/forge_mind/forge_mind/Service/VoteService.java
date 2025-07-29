package com.forge_mind.forge_mind.Service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.forge_mind.forge_mind.Repository.UserVoteRepository;
import com.forge_mind.forge_mind.database.UserVotes;

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

}
