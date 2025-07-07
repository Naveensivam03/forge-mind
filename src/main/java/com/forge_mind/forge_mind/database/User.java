package com.forge_mind.forge_mind.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false,unique = true)
    private String email ;
    @Column(nullable = false)
    private String password;
    @Column(name = "isEnabled")
    private boolean isAccount_enabled;
    private boolean IsExpired;
//    @Column()
//    private LocalDateTime verificationCodeExpiresAt;
    @Column(name = "verificationCode")
    private String verificationCode;

    public User(Long id, String username, String email, String password, boolean isAccount_enabled, boolean isExpired , String verificationCode) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAccount_enabled = isAccount_enabled;
        this.IsExpired = isExpired;
        this.verificationCode = verificationCode;

    }

    public User(String username, String email, String password, boolean isAccount_enabled, boolean isExpired , String verificationCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAccount_enabled = isAccount_enabled;
        this.IsExpired = isExpired;
        this.verificationCode = verificationCode;
    }

    public User() {
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password  = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
            }




}
