package com.forge_mind.forge_mind.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.forge_mind.forge_mind.database.UserVotes;
import java.util.List;

public interface UserVoteRepository extends JpaRepository<UserVotes, Long> {

  List<UserVotes> findByUserId(long id);

}
