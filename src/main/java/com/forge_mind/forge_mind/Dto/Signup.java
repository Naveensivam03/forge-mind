package com.forge_mind.forge_mind.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Signup {
  private String username;
  private String email;
  private String password;

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public void setUsername(String name) {
    this.username = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
