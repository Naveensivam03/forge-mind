package com.forge_mind.forge_mind.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDto {
  private String email;
  private String verificationCode;

  public String getEmail() {
    return email;
  }

  public String getVerificationCode() {
    return verificationCode;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setVerificationCode(String verificationCode) {
    this.verificationCode = verificationCode;
  }
}
