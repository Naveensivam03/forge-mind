package com.forge_mind.forge_mind.Dto;

import lombok.Getter;
import lombok.Setter;
import com.forge_mind.forge_mind.database.Posts;

@Getter
@Setter
public class PostResponseDto {

  private Long id;
  private String content;
  private Integer upvotes;
  private Integer downvotes;
  private Long categoryId;
  private String categoryName;

  public PostResponseDto() {
  }

  public PostResponseDto(Posts post) {
    this.id = post.getId();
    this.content = post.getContent();
    this.upvotes = post.getUpvotes();
    this.downvotes = post.getDownvotes();
    this.categoryId = post.getCategory().getId();
    this.categoryName = post.getCategory().getName();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getUpvotes() {
    return upvotes;
  }

  public void setUpvotes(Integer upvotes) {
    this.upvotes = upvotes;
  }

  public Integer getDownvotes() {
    return downvotes;
  }

  public void setDownvotes(Integer downvotes) {
    this.downvotes = downvotes;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

}
