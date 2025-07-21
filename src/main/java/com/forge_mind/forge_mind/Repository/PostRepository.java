package com.forge_mind.forge_mind.Repository;

import com.forge_mind.forge_mind.database.Posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Posts, Long> {
  // Optional<Posts> findByCategory(Integer category_id);
  Optional<Posts> findByCategory_name(String categoryName);

  Optional<Posts> findById(long id);
}
