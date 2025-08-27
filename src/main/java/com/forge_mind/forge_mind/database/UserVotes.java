package com.forge_mind.forge_mind.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "userVotes")
@Getter
@Setter
public class UserVotes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posts_id", nullable = false)
    private Posts posts;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;


    public enum VoteType{
        UPVOTE , DOWNVOTE
    }

    public void UserVotes(long id, User user, Posts posts, VoteType voteType) {
        this.id = id;
        this.user = user;
        this.posts = posts;
        this.voteType = voteType;
    }

    public UserVotes(User user, Posts posts, VoteType voteType) {
        this.user = user;
        this.posts = posts;
        this.voteType = voteType;
    }

    public UserVotes() {
    }
}
