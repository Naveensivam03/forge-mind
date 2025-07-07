package com.forge_mind.forge_mind.Service;

import com.forge_mind.forge_mind.Repository.PostRepository;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List allPosts(){
        System.out.println("this all posts from service is properly been calling out!" );
        return List.of(postRepository.findAll());
    }
}
