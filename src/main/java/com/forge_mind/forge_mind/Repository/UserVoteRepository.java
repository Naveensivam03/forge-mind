package com.forge_mind.forge_mind.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.forge_mind.forge_mind.database.UserVotes;
import java.util.List;

public interface UserVoteRepository extends JpaRepository<UserVotes, Long> {

  List<UserVotes> findByUserId(long id);
  
  Optional<UserVotes> findByUserIdAndPostsId(long userId, long postId);
  
  @Modifying
  @Transactional
  @Query("DELETE FROM UserVotes uv WHERE uv.user.id = :userId AND uv.posts.id = :postId")
  void deleteByUserIdAndPostsId(@Param("userId") long userId, @Param("postId") long postId);

}
