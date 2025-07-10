package com.forge_mind.forge_mind.database;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    private Category category;

    private Integer upvotes;

    private Integer downvotes;

    private LocalDateTime created_at;

    public Posts(Long id, String content, Category category, Integer upvotes, Integer downvotes, LocalDateTime created_at) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.created_at = created_at;
    }

    public Posts() {
    }

    public Posts(String content, Category category, Integer upvotes, Integer downvotes, LocalDateTime created_at) {
        this.content = content;
        this.category = category;
        this.upvotes = upvotes;
        this.downvotes = downvotes;
        this.created_at = created_at;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
