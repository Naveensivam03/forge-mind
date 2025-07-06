package com.forge_mind.forge_mind.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDto {
    private String email;
    private String verificationCode;

}
