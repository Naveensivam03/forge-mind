package com.forge_mind.forge_mind.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.forge_mind.forge_mind.database.Category;

@Repository
public interface CategRepository extends JpaRepository<Category, Long> {

}
