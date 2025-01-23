package com.example.demo.payload;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDto {

    private String password;

    private String prePassword;
}
