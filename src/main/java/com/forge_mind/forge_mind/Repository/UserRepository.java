package com.forge_mind.forge_mind.Repository;

import com.forge_mind.forge_mind.database.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User , Long> {

    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
boolean existsByUsername(String username);
boolean existsByEmail(String email);

}
