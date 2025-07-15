package com.forge_mind.forge_mind.Dto;

import lombok.Getter;
import lombok.Setter;
import com.forge_mind.forge_mind.database.Posts;

@Getter
@Setter
public class PostResponseDto {

    private Posts posts;
    private String categName;

    public PostResponseDto() {

    }

    public PostResponseDto(Posts posts, String name) {
        this.posts = posts;
        this.categName = name;
    }
}
