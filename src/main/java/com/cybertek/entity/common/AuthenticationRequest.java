package com.cybertek.entity.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor

public class AuthenticationRequest {
    private String username;
    private String password;

}
