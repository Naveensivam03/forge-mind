package com.forge_mind.forge_mind.Service;

import com.forge_mind.forge_mind.Repository.PostRepository;
import com.forge_mind.forge_mind.database.Posts;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    //to get the posts based on the category name
    public List<Optional<Posts>> categPosts(String categName){
        Optional<Posts> categPosts = postRepository.findByCategory_name(categName);
        if(categPosts.isPresent()){
            return List.of(categPosts);
        }
        throw new RuntimeException("no posts found with that category "+ categName);
    }
}
